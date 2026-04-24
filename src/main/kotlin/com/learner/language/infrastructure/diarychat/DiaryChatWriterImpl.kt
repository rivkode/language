package com.learner.language.infrastructure.diarychat

import com.learner.language.domain.diarychat.DiaryChatEventType
import com.learner.language.domain.diarychat.DiaryChatMessage
import com.learner.language.domain.diarychat.DiaryChatMessageSource
import com.learner.language.domain.diarychat.DiaryChatParticipant
import com.learner.language.domain.diarychat.DiaryChatRoom
import com.learner.language.domain.diarychat.DiaryChatWriter
import org.springframework.stereotype.Component

@Component
class DiaryChatWriterImpl(
    private val roomRepository: DiaryChatRoomRepository,
    private val participantRepository: DiaryChatParticipantRepository,
    private val messageRepository: DiaryChatMessageRepository,
) : DiaryChatWriter {

    override fun saveRoom(room: DiaryChatRoom): DiaryChatRoom = roomRepository.save(room)

    override fun saveParticipant(participant: DiaryChatParticipant): DiaryChatParticipant =
        participantRepository.save(participant)

    override fun removeParticipant(roomId: Long, userId: Long): Int =
        participantRepository.deleteByRoomIdAndUserId(roomId, userId)

    override fun saveMessage(message: DiaryChatMessage): DiaryChatMessage =
        messageRepository.save(message)

    override fun appendEvent(
        roomId: Long,
        eventType: DiaryChatEventType,
        payloadJson: String?,
    ): DiaryChatMessage {
        val event = DiaryChatMessage(
            roomId = roomId,
            authorUserId = null,
            text = payloadJson ?: "{}",
            audioUrl = null,
            source = DiaryChatMessageSource.SYSTEM,
            eventType = eventType,
        )
        return messageRepository.save(event)
    }

    override fun incrementParticipantCount(roomId: Long) {
        roomRepository.incrementParticipantCount(roomId)
    }

    override fun decrementParticipantCount(roomId: Long) {
        roomRepository.decrementParticipantCount(roomId)
    }
}
