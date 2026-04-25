package com.learner.language.application.diarychat

import com.learner.language.domain.diarychat.DiaryChatReader
import com.learner.language.domain.diarychat.exception.ChatroomForbiddenException
import com.learner.language.domain.diarychat.exception.PollCursorExpiredException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.request.async.DeferredResult
import java.time.Duration
import java.time.LocalDateTime

@Service
class DiaryChatPollingFacade(
    private val chatReader: DiaryChatReader,
    private val assembler: DiaryChatPollViewAssembler,
    private val pollingHub: DiaryChatPollingHub,
) {

    @Transactional(readOnly = true)
    fun poll(
        roomId: Long,
        userId: Long,
        after: Long,
        waitSeconds: Int,
    ): DeferredResult<DiaryChatPollView> {
        val safeAfter = after.coerceAtLeast(0L)
        chatReader.getRoomById(roomId)
        if (!chatReader.isParticipant(roomId, userId)) {
            throw ChatroomForbiddenException("채팅방 참여자만 폴링할 수 있습니다.")
        }

        validateCursor(roomId, safeAfter)

        val cappedWait = waitSeconds.coerceIn(1, 60).toLong()
        val deferred = DeferredResult<DiaryChatPollView>(
            Duration.ofSeconds(cappedWait).toMillis(),
        )

        val immediate = assembler.pollForRoom(roomId, safeAfter)
        if (immediate.items.isNotEmpty() || immediate.events.isNotEmpty()) {
            deferred.setResult(immediate)
            return deferred
        }

        deferred.onTimeout {
            deferred.setResult(DiaryChatPollView(emptyList(), emptyList(), safeAfter))
        }
        pollingHub.subscribe(roomId, safeAfter, deferred)
        return deferred
    }

    private fun validateCursor(roomId: Long, after: Long) {
        if (after == 0L) return
        val createdAt = chatReader.findMessageCreatedAtById(after)
        if (createdAt == null) {
            val last = chatReader.findLastMessageId(roomId)
            if (after <= last) {
                throw PollCursorExpiredException(
                    "커서($after)의 메시지는 보존 기간이 지나 삭제되었습니다.",
                )
            }
            return
        }
        if (createdAt.isBefore(LocalDateTime.now().minusDays(CURSOR_EXPIRY_DAYS))) {
            throw PollCursorExpiredException(
                "커서($after)가 $CURSOR_EXPIRY_DAYS 일 이상 경과했습니다.",
            )
        }
    }

    companion object {
        private const val CURSOR_EXPIRY_DAYS = 7L
    }
}
