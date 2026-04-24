package com.learner.language.infrastructure.diarychat

import com.learner.language.domain.diarychat.DiaryChatMessage
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface DiaryChatMessageRepository : JpaRepository<DiaryChatMessage, Long> {

    @Query(
        """
        SELECT m FROM DiaryChatMessage m
        WHERE m.roomId = :roomId
          AND (:before IS NULL OR m.id < :before)
        ORDER BY m.id DESC
        """
    )
    fun findBeforeDesc(
        @Param("roomId") roomId: Long,
        @Param("before") before: Long?,
        pageable: Pageable,
    ): List<DiaryChatMessage>

    @Query(
        """
        SELECT m FROM DiaryChatMessage m
        WHERE m.roomId = :roomId AND m.id > :after
        ORDER BY m.id ASC
        """
    )
    fun findAfter(
        @Param("roomId") roomId: Long,
        @Param("after") after: Long,
    ): List<DiaryChatMessage>

    @Query("SELECT MAX(m.id) FROM DiaryChatMessage m WHERE m.roomId = :roomId")
    fun findLastMessageId(@Param("roomId") roomId: Long): Long?

    @Query("SELECT m.createdAt FROM DiaryChatMessage m WHERE m.id = :id")
    fun findCreatedAtById(@Param("id") id: Long): LocalDateTime?

    @Modifying
    @Query("DELETE FROM DiaryChatMessage m WHERE m.createdAt < :cutoff")
    fun deleteOlderThan(@Param("cutoff") cutoff: LocalDateTime): Int
}
