package com.learner.language.application.diarychat

import com.fasterxml.jackson.databind.ObjectMapper
import com.learner.language.domain.diary.Diary
import com.learner.language.domain.diary.DiaryReader
import com.learner.language.domain.diarychat.DiaryChatEventType
import com.learner.language.domain.diarychat.DiaryChatMessage
import com.learner.language.domain.diarychat.DiaryChatMessageSource
import com.learner.language.domain.diarychat.DiaryChatReader
import com.learner.language.domain.diarychat.DiaryChatRoom
import com.learner.language.domain.diarychat.DiaryChatWriter
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DiaryChatAiService(
    private val chatClient: ChatClient,
    private val diaryReader: DiaryReader,
    private val chatReader: DiaryChatReader,
    private val chatWriter: DiaryChatWriter,
    private val pollingHub: DiaryChatPollingHub,
    private val assembler: DiaryChatPollViewAssembler,
    private val objectMapper: ObjectMapper,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun sendWelcome(roomId: Long) {
        val room = chatReader.findRoomByDiaryId(diaryIdOf(roomId) ?: return) ?: return
        if (!room.aiAssistantEnabled) return

        announceTyping(roomId)
        try {
            val diary = diaryReader.getById(room.diaryId)
            val text = chatClient.prompt()
                .user(buildWelcomePrompt(diary))
                .call()
                .content()
                ?.trim()
                ?.ifEmpty { null }
                ?: WELCOME_FALLBACK
            persistAiMessage(roomId, text)
        } catch (e: Exception) {
            log.warn("AI welcome generation failed for room $roomId: {}", e.message, e)
            announceFailure(roomId, e.message)
        }
    }

    @Transactional
    fun respondToUserMessage(roomId: Long, triggerMessageId: Long) {
        val room = chatReader.getRoomById(roomId)
        if (!room.aiAssistantEnabled) return

        val triggering = chatReader.findMessageById(triggerMessageId) ?: return
        if (triggering.source != DiaryChatMessageSource.USER) return

        announceTyping(roomId)
        try {
            val diary = diaryReader.getById(room.diaryId)
            val history = chatReader.findRecentNonSystemMessages(room.id, HISTORY_TURNS)
                .sortedBy { it.id }
            val text = chatClient.prompt()
                .user(buildResponsePrompt(diary, history))
                .call()
                .content()
                ?.trim()
                ?.ifEmpty { null }
                ?: RESPONSE_FALLBACK
            persistAiMessage(roomId, text)
        } catch (e: Exception) {
            log.warn("AI response generation failed for room $roomId: {}", e.message, e)
            announceFailure(roomId, e.message)
        }
    }

    private fun diaryIdOf(roomId: Long): Long? =
        runCatching { chatReader.getRoomById(roomId).diaryId }.getOrNull()

    private fun announceTyping(roomId: Long) {
        chatWriter.appendEvent(
            roomId = roomId,
            eventType = DiaryChatEventType.AI_TYPING,
            payloadJson = objectMapper.writeValueAsString(
                mapOf("type" to DiaryChatEventType.AI_TYPING.apiValue()),
            ),
        )
        notify(roomId)
    }

    private fun announceFailure(roomId: Long, reason: String?) {
        chatWriter.appendEvent(
            roomId = roomId,
            eventType = DiaryChatEventType.AI_FAILED,
            payloadJson = objectMapper.writeValueAsString(
                mapOf(
                    "type" to DiaryChatEventType.AI_FAILED.apiValue(),
                    "reason" to (reason ?: "unknown"),
                ),
            ),
        )
        notify(roomId)
    }

    private fun persistAiMessage(roomId: Long, text: String) {
        chatWriter.saveMessage(
            DiaryChatMessage(
                roomId = roomId,
                authorUserId = null,
                text = text.take(DiaryChatRoom.MESSAGE_MAX_LENGTH),
                source = DiaryChatMessageSource.AI,
            )
        )
        notify(roomId)
    }

    private fun notify(roomId: Long) {
        pollingHub.notifyRoom(roomId) { after -> assembler.pollForRoom(roomId, after) }
    }

    private fun buildWelcomePrompt(diary: Diary): String = """
        You are Jamo AI, a friendly Korean-language tutor inside a casual chatroom attached to the user's three-line diary.
        The diary author wrote:
        [1] ${diary.line1}
        [2] ${diary.line2}
        [3] ${diary.line3}

        Greet everyone in 2-3 short sentences in casual, warm Korean. Mention one concrete detail from the diary, then ask one open-ended follow-up question that invites others to chat. Output Korean only.
    """.trimIndent()

    private fun buildResponsePrompt(diary: Diary, history: List<DiaryChatMessage>): String {
        val historyBlock = history.joinToString("\n") { msg ->
            val who = when (msg.source) {
                DiaryChatMessageSource.AI -> "JamoAI"
                DiaryChatMessageSource.USER -> "User${msg.authorUserId ?: "?"}"
                DiaryChatMessageSource.SYSTEM -> ""
            }
            "$who: ${msg.text}"
        }
        return """
            You are Jamo AI, a friendly Korean-language tutor in a chatroom attached to the user's three-line diary.
            Diary contents:
            [1] ${diary.line1}
            [2] ${diary.line2}
            [3] ${diary.line3}

            Recent conversation (oldest first):
            $historyBlock

            Reply naturally to the most recent user message in 1-3 short Korean sentences. Stay supportive and curious. Do not impersonate other participants. If the topic drifts, gently steer it back to the diary.
        """.trimIndent()
    }

    companion object {
        private const val HISTORY_TURNS = 20
        private const val WELCOME_FALLBACK = "안녕하세요! 일기 잘 봤어요. 어떤 이야기를 나눠볼까요?"
        private const val RESPONSE_FALLBACK = "조금 더 말해줄 수 있어요?"
    }
}
