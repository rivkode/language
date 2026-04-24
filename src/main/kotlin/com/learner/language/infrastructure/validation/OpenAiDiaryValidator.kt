package com.learner.language.infrastructure.validation

import com.learner.language.application.validation.DiaryValidator
import com.learner.language.application.validation.LineStatus
import com.learner.language.application.validation.LineValidation
import com.learner.language.application.validation.ValidationFailedException
import com.learner.language.application.validation.ValidationTimeoutException
import com.learner.language.domain.diary.Diary
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.stereotype.Component
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

@Component
class OpenAiDiaryValidator(
    private val chatClient: ChatClient,
) : DiaryValidator {

    private val log = LoggerFactory.getLogger(javaClass)

    data class AiLine(
        val lineIndex: Int = 0,
        val status: String = "error",
        val message: String = "",
        val suggestion: String? = null,
    )

    data class AiValidationResponse(
        val lines: List<AiLine> = emptyList(),
    )

    data class AiSingleLineResponse(
        val lineIndex: Int = 0,
        val status: String = "error",
        val message: String = "",
        val suggestion: String? = null,
    )

    override fun validate(lines: List<String>): List<LineValidation> {
        require(lines.size == Diary.LINE_COUNT) { "lines must be 3" }
        val prompt = buildBatchPrompt(lines)
        val response = try {
            chatClient.prompt()
                .user(prompt)
                .call()
                .entity(AiValidationResponse::class.java)
        } catch (e: Exception) {
            handleAiException(e)
        } ?: throw ValidationFailedException("AI 검증 응답이 비어있습니다.")

        val resultByIndex = response.lines.associateBy { it.lineIndex }
        return lines.mapIndexed { idx, original ->
            val ai = resultByIndex[idx]
            LineValidation(
                lineIndex = idx,
                originalText = original,
                status = LineStatus.fromApi(ai?.status),
                message = ai?.message ?: fallbackMessage(ai?.status),
                suggestion = normalizeSuggestion(ai?.status, ai?.suggestion),
            )
        }
    }

    override fun validateLine(lineIndex: Int, text: String): LineValidation {
        val prompt = buildSingleLinePrompt(lineIndex, text)
        val response = try {
            chatClient.prompt()
                .user(prompt)
                .call()
                .entity(AiSingleLineResponse::class.java)
        } catch (e: Exception) {
            handleAiException(e)
        } ?: throw ValidationFailedException("AI 검증 응답이 비어있습니다.")

        return LineValidation(
            lineIndex = lineIndex,
            originalText = text,
            status = LineStatus.fromApi(response.status),
            message = response.message.ifBlank { fallbackMessage(response.status) },
            suggestion = normalizeSuggestion(response.status, response.suggestion),
        )
    }

    private fun handleAiException(e: Exception): Nothing {
        log.warn("AI validation call failed: {}", e.message, e)
        if (e is TimeoutException || e is SocketTimeoutException ||
            (e.message?.contains("timeout", ignoreCase = true) == true)
        ) {
            throw ValidationTimeoutException("AI 응답이 시간 내에 도착하지 않았습니다.")
        }
        throw ValidationFailedException("AI 검증에 실패했습니다: ${e.message}")
    }

    private fun buildBatchPrompt(lines: List<String>): String = """
        You are a Korean-language tutor helping a learner write natural three-line diary entries.
        Evaluate each of the three Korean sentences below. Respond in JSON only.

        Sentences:
        [0] ${lines[0]}
        [1] ${lines[1]}
        [2] ${lines[2]}

        For each line, decide the status:
        - "ok": grammatically correct and sounds natural.
        - "suggestion": grammatically fine but could be more natural or polished.
        - "error": grammatically wrong and must be fixed before posting.

        Rules:
        - "message" should be a short tutor feedback (Korean or English or mix, under 200 characters).
        - "suggestion" must be a complete improved Korean sentence when status is "suggestion" or "error". Use null when status is "ok".
        - Return lines in order with lineIndex 0, 1, 2.
    """.trimIndent()

    private fun buildSingleLinePrompt(lineIndex: Int, text: String): String = """
        You are a Korean-language tutor. Evaluate the following Korean sentence and respond in JSON only.

        Sentence: $text

        Decide status:
        - "ok": natural and correct.
        - "suggestion": grammatically fine but could be more natural.
        - "error": grammatical mistake that must be fixed.

        Rules:
        - Return lineIndex = $lineIndex.
        - "message" under 200 characters (Korean or English).
        - "suggestion" is a complete improved sentence, or null when status is "ok".
    """.trimIndent()

    private fun fallbackMessage(status: String?): String = when (LineStatus.fromApi(status)) {
        LineStatus.OK -> "Sounds natural!"
        LineStatus.SUGGESTION -> "조금 더 자연스럽게 다듬어 볼 수 있어요."
        LineStatus.ERROR -> "문법을 다시 확인해 주세요."
    }

    private fun normalizeSuggestion(status: String?, suggestion: String?): String? {
        val s = LineStatus.fromApi(status)
        if (s == LineStatus.OK) return null
        return suggestion?.takeIf { it.isNotBlank() }
    }
}
