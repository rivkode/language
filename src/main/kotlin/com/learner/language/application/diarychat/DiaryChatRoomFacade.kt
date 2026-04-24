package com.learner.language.application.diarychat

import com.fasterxml.jackson.databind.ObjectMapper
import com.learner.language.domain.diary.DiaryReader
import com.learner.language.domain.diary.exception.DiaryForbiddenException
import com.learner.language.domain.diarychat.DiaryChatEventType
import com.learner.language.domain.diarychat.DiaryChatParticipant
import com.learner.language.domain.diarychat.DiaryChatReader
import com.learner.language.domain.diarychat.DiaryChatRoom
import com.learner.language.domain.diarychat.DiaryChatWriter
import com.learner.language.domain.diarychat.exception.ChatroomForbiddenException
import com.learner.language.domain.diarychat.exception.ChatroomNotFoundException
import com.learner.language.domain.diarychat.exception.ChatroomParticipantLimitException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZoneOffset

@Service
class DiaryChatRoomFacade(
    private val diaryReader: DiaryReader,
    private val chatReader: DiaryChatReader,
    private val chatWriter: DiaryChatWriter,
    private val authorResolver: DiaryChatAuthorResolver,
    private val pollingHub: DiaryChatPollingHub,
    private val objectMapper: ObjectMapper,
    private val pollViewAssembler: DiaryChatPollViewAssembler,
) {

    @Transactional
    fun createOrGet(command: CreateChatRoomCommand): DiaryChatRoomView {
        val diary = diaryReader.getById(command.diaryId)
        if (!diary.isPublic) {
            throw DiaryForbiddenException("비공개 일기에는 채팅방을 만들 수 없습니다.")
        }

        chatReader.findRoomByDiaryId(command.diaryId)?.let { return toRoomView(it) }

        val room = chatWriter.saveRoom(
            DiaryChatRoom(
                diaryId = command.diaryId,
                hostUserId = command.requesterUserId,
                aiAssistantEnabled = command.aiAssistantEnabled,
            )
        )

        chatWriter.saveParticipant(
            DiaryChatParticipant(roomId = room.id, userId = command.requesterUserId, isHost = true)
        )
        chatWriter.incrementParticipantCount(room.id)
        writeEvent(room.id, DiaryChatEventType.PARTICIPANT_JOINED, mapOf("userId" to command.requesterUserId))
        pollingHub.notifyRoom(room.id) { pollViewAssembler.pollForRoom(room.id, afterInit(room.id)) }

        val refreshed = chatReader.getRoomById(room.id)
        return toRoomView(refreshed)
    }

    @Transactional(readOnly = true)
    fun get(roomId: Long): DiaryChatRoomView = toRoomView(chatReader.getRoomById(roomId))

    @Transactional(readOnly = true)
    fun getParticipants(roomId: Long): List<DiaryChatParticipantView> {
        chatReader.getRoomById(roomId)
        val participants = chatReader.findParticipants(roomId)
        val authors = authorResolver.resolve(participants.map { it.userId })
        return participants.map {
            DiaryChatParticipantView(
                user = authors[it.userId] ?: authorResolver.aiAuthor(),
                isHost = it.isHost,
                joinedAt = it.joinedAt.toInstant(ZoneOffset.UTC),
            )
        }
    }

    @Transactional
    fun join(roomId: Long, userId: Long): DiaryChatRoomView {
        val room = chatReader.getRoomById(roomId)
        if (chatReader.isParticipant(roomId, userId)) return toRoomView(room)

        if (room.participantCount >= DiaryChatRoom.PARTICIPANT_LIMIT) {
            throw ChatroomParticipantLimitException(
                "채팅방 정원(${DiaryChatRoom.PARTICIPANT_LIMIT})이 가득 찼습니다."
            )
        }

        chatWriter.saveParticipant(
            DiaryChatParticipant(roomId = roomId, userId = userId, isHost = false)
        )
        chatWriter.incrementParticipantCount(roomId)
        writeEvent(roomId, DiaryChatEventType.PARTICIPANT_JOINED, mapOf("userId" to userId))
        pollingHub.notifyRoom(roomId) { pollViewAssembler.pollForRoom(roomId, chatReader.findLastMessageId(roomId) - 1) }

        return toRoomView(chatReader.getRoomById(roomId))
    }

    @Transactional
    fun leave(roomId: Long, userId: Long) {
        val room = chatReader.getRoomById(roomId)
        if (!chatReader.isParticipant(roomId, userId)) return
        if (room.isHostedBy(userId)) {
            throw ChatroomForbiddenException("방장은 퇴장할 수 없습니다. 방 삭제 기능을 이용해 주세요.")
        }

        val removed = chatWriter.removeParticipant(roomId, userId)
        if (removed > 0) {
            chatWriter.decrementParticipantCount(roomId)
            writeEvent(roomId, DiaryChatEventType.PARTICIPANT_LEFT, mapOf("userId" to userId))
            pollingHub.notifyRoom(roomId) { pollViewAssembler.pollForRoom(roomId, chatReader.findLastMessageId(roomId) - 1) }
        }
    }

    @Transactional
    fun setAiAssistant(roomId: Long, userId: Long, enabled: Boolean): DiaryChatRoomView {
        val room = chatReader.getRoomById(roomId)
        if (!room.isHostedBy(userId)) {
            throw ChatroomForbiddenException("AI 토글은 방장만 변경 가능합니다.")
        }
        if (room.aiAssistantEnabled == enabled) return toRoomView(room)

        room.updateAiAssistant(enabled)
        chatWriter.saveRoom(room)
        writeEvent(roomId, DiaryChatEventType.AI_TOGGLE_CHANGED, mapOf("enabled" to enabled))
        pollingHub.notifyRoom(roomId) { pollViewAssembler.pollForRoom(roomId, chatReader.findLastMessageId(roomId) - 1) }

        return toRoomView(room)
    }

    private fun writeEvent(roomId: Long, type: DiaryChatEventType, payload: Map<String, Any?>) {
        val body = payload + mapOf("type" to type.apiValue())
        chatWriter.appendEvent(roomId, type, objectMapper.writeValueAsString(body))
    }

    private fun afterInit(roomId: Long): Long {
        val last = chatReader.findLastMessageId(roomId)
        return (last - 1).coerceAtLeast(0)
    }

    private fun toRoomView(room: DiaryChatRoom): DiaryChatRoomView = DiaryChatRoomView(
        roomId = room.id,
        diaryId = room.diaryId,
        hostUserId = room.hostUserId,
        aiAssistantEnabled = room.aiAssistantEnabled,
        participantCount = room.participantCount,
        createdAt = room.createdAt.toInstant(ZoneOffset.UTC),
    )

    @Transactional(readOnly = true)
    fun requireMembership(roomId: Long, userId: Long) {
        val exists = chatReader.isParticipant(roomId, userId)
        if (!exists) {
            // 방 자체가 없으면 404, 방은 있는데 비참여면 403
            chatReader.findRoomByDiaryId(roomId) ?: chatReader.getRoomById(roomId)
            throw ChatroomForbiddenException("채팅방 참여자만 접근할 수 있습니다.")
        }
    }
}
