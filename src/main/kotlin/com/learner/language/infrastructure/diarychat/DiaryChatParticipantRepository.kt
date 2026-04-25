package com.learner.language.infrastructure.diarychat

import com.learner.language.domain.diarychat.DiaryChatParticipant
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface DiaryChatParticipantRepository : JpaRepository<DiaryChatParticipant, Long> {
    fun findByRoomIdAndUserId(roomId: Long, userId: Long): Optional<DiaryChatParticipant>
    fun existsByRoomIdAndUserId(roomId: Long, userId: Long): Boolean
    fun findByRoomIdOrderByJoinedAtAsc(roomId: Long): List<DiaryChatParticipant>
    fun countByRoomId(roomId: Long): Long

    @Modifying
    @Query("DELETE FROM DiaryChatParticipant p WHERE p.roomId = :roomId AND p.userId = :userId")
    fun deleteByRoomIdAndUserId(
        @Param("roomId") roomId: Long,
        @Param("userId") userId: Long,
    ): Int
}
