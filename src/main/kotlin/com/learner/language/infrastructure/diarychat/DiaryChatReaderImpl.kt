package com.learner.language.infrastructure.diarychat

import com.learner.language.domain.diarychat.DiaryChatMessage
import com.learner.language.domain.diarychat.DiaryChatParticipant
import com.learner.language.domain.diarychat.DiaryChatReader
import com.learner.language.domain.diarychat.DiaryChatRoom
import com.learner.language.domain.diarychat.exception.ChatroomNotFoundException
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component

@Component
class DiaryChatReaderImpl(
    private val roomRepository: DiaryChatRoomRepository,
    private val participantRepository: DiaryChatParticipantRepository,
    private val messageRepository: DiaryChatMessageRepository,
) : DiaryChatReader {

    override fun getRoomById(roomId: Long): DiaryChatRoom {
        return roomRepository.findById(roomId)
            .orElseThrow { ChatroomNotFoundException("Chatroom not found: $roomId") }
    }

    override fun findRoomByDiaryId(diaryId: Long): DiaryChatRoom? {
        return roomRepository.findByDiaryId(diaryId).orElse(null)
    }

    override fun findParticipants(roomId: Long): List<DiaryChatParticipant> =
        participantRepository.findByRoomIdOrderByJoinedAtAsc(roomId)

    override fun isParticipant(roomId: Long, userId: Long): Boolean =
        participantRepository.existsByRoomIdAndUserId(roomId, userId)

    override fun findMessagesBefore(roomId: Long, before: Long?, size: Int): List<DiaryChatMessage> =
        messageRepository.findBeforeDesc(roomId, before, PageRequest.of(0, size))

    override fun findMessagesAfter(roomId: Long, after: Long): List<DiaryChatMessage> =
        messageRepository.findAfter(roomId, after)

    override fun findLastMessageId(roomId: Long): Long =
        messageRepository.findLastMessageId(roomId) ?: 0L

    override fun findMessageById(messageId: Long): DiaryChatMessage? =
        messageRepository.findById(messageId).orElse(null)

    override fun findMessageCreatedAtById(messageId: Long): java.time.LocalDateTime? =
        messageRepository.findCreatedAtById(messageId)

    override fun findRecentNonSystemMessages(roomId: Long, limit: Int): List<DiaryChatMessage> =
        messageRepository.findRecentNonSystem(roomId, PageRequest.of(0, limit))
}
