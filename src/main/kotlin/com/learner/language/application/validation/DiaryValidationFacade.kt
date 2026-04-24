package com.learner.language.application.validation

import com.learner.language.domain.diary.Diary
import com.learner.language.domain.diary.exception.InvalidLineCountException
import com.learner.language.domain.diary.exception.InvalidLineLengthException
import com.learner.language.system.exception.ErrorCode
import com.learner.language.system.exception.LanguageException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class DiaryValidationFacade(
    private val validator: DiaryValidator,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun validate(lines: List<String>): DiaryValidationResult {
        validateLineCount(lines)
        validateLineLength(lines)

        val validationId = "val-${UUID.randomUUID().toString().replace("-", "").take(10)}"
        val results = runWithErrorHandling { validator.validate(lines) }
        return DiaryValidationResult(validationId, results)
    }

    fun validateLine(lineIndex: Int, text: String): LineValidation {
        if (lineIndex < 0 || lineIndex >= Diary.LINE_COUNT) {
            throw InvalidLineCountException("lineIndex 는 0..${Diary.LINE_COUNT - 1} 사이여야 합니다.")
        }
        validateLineLength(listOf(text))
        return runWithErrorHandling { validator.validateLine(lineIndex, text) }
    }

    private fun <T> runWithErrorHandling(block: () -> T): T {
        return try {
            block()
        } catch (e: ValidationTimeoutException) {
            throw e
        } catch (e: ValidationFailedException) {
            throw e
        } catch (e: Exception) {
            log.warn("Diary validation failed: {}", e.message, e)
            throw ValidationFailedException("AI 검증 중 오류가 발생했습니다.")
        }
    }

    private fun validateLineCount(lines: List<String>) {
        if (lines.size != Diary.LINE_COUNT) {
            throw InvalidLineCountException("라인은 ${Diary.LINE_COUNT}개여야 합니다.")
        }
    }

    private fun validateLineLength(lines: List<String>) {
        lines.forEachIndexed { idx, line ->
            val trimmed = line.trim()
            if (trimmed.isEmpty()) {
                throw InvalidLineLengthException("${idx + 1}번째 라인이 비어있습니다.")
            }
            if (trimmed.length > Diary.LINE_MAX_LENGTH) {
                throw InvalidLineLengthException(
                    "${idx + 1}번째 라인이 ${Diary.LINE_MAX_LENGTH}자를 초과했습니다.",
                )
            }
        }
    }
}

class ValidationFailedException(message: String) : LanguageException(
    ErrorCode.VALIDATION_FAILED,
    message,
    publicCode = ErrorCode.VALIDATION_FAILED.name,
)

class ValidationTimeoutException(message: String) : LanguageException(
    ErrorCode.VALIDATION_TIMEOUT,
    message,
    publicCode = ErrorCode.VALIDATION_TIMEOUT.name,
)
