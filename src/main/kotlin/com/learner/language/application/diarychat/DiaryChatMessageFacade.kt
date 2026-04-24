package com.learner.language.application.diarychat

import com.learner.language.domain.diarychat.DiaryChatMessage
import com.learner.language.domain.diarychat.DiaryChatMessageSource
import com.learner.language.domain.diarychat.DiaryChatReader
import com.learner.language.domain.diarychat.DiaryChatRoom
import com.learner.language.domain.diarychat.DiaryChatWriter
import com.learner.language.domain.diarychat.exception.ChatroomForbiddenException
import com.learner.language.domain.diarychat.exception.ChatroomMessageTooLongException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DiaryChatMessageFacade(
    private val chatReader: DiaryChatReader,
    private val chatWriter: DiaryChatWriter,
    private val assembler: DiaryChatPollViewAssembler,
    private val authorResolver: DiaryChatAuthorResolver,
    private val pollingHub: DiaryChatPollingHub,
) {

    data class HistoryResult(
        val items: List<DiaryChatMessageView>,
        val hasMore: Boolean,
        val oldestMessageId: Long?,
    )

    @Transactional
    fun send(command: SendMessageCommand): DiaryChatMessageView {
        val room = chatReader.getRoomById(command.roomId)
        if (!chatReader.isParticipant(room.id, command.authorUserId)) {
            throw ChatroomForbiddenException("채팅방 참여자만 메시지를 보낼 수 있습니다.")
        }

        val text = command.text.trim()
        if (text.isEmpty()) {
            throw ChatroomMessageTooLongException("메시지가 비어 있습니다.")
        }
        if (text.length > DiaryChatRoom.MESSAGE_MAX_LENGTH) {
            throw ChatroomMessageTooLongException(
                "메시지는 최대 ${DiaryChatRoom.MESSAGE_MAX_LENGTH}자입니다."
            )
        }

        val saved = chatWriter.saveMessage(
            DiaryChatMessage(
                roomId = room.id,
                authorUserId = command.authorUserId,
                text = text,
                audioUrl = command.audioUrl,
                source = DiaryChatMessageSource.USER,
            )
        )
        room.touchActivity()
        chatWriter.saveRoom(room)

        pollingHub.notifyRoom(room.id) { assembler.pollForRoom(room.id, saved.id - 1) }

        val author = authorResolver.resolve(listOf(command.authorUserId))[command.authorUserId]
            ?: authorResolver.aiAuthor()
        return assembler.toMessageView(saved, author)
    }

    @Transactional(readOnly = true)
    fun history(roomId: Long, userId: Long, before: Long?, size: Int): HistoryResult {
        chatReader.getRoomById(roomId)
        if (!chatReader.isParticipant(roomId, userId)) {
            throw ChatroomForbiddenException("채팅방 참여자만 메시지를 조회할 수 있습니다.")
        }

        val capped = size.coerceIn(1, 100)
        val fetched = assembler.messagesBefore(roomId, before, capped + 1)
        val hasMore = fetched.size > capped
        val items = if (hasMore) fetched.drop(1) else fetched
        return HistoryResult(items, hasMore, items.firstOrNull()?.messageId)
    }
}
