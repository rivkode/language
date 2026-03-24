package com.learner.language.infrastructure.chat

import com.learner.language.domain.chat.ChatMessage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ChatRepository : JpaRepository<ChatMessage, Long> {
    @Query(value = "SELECT * FROM chat_message WHERE chat_room_id = :chatRoomId ORDER BY created_at DESC LIMIT 1", nativeQuery = true)
    fun findLastMessage(@Param("chatRoomId") chatRoonId: Long): ChatMessage?

    @Query(
        value = "SELECT * FROM chat_message WHERE chat_room_id = :chatRoomId AND sequence < :sequence ORDER BY sequence DESC LIMIT :limit",
        nativeQuery = true
    )
    fun findPreviousMessages(
        @Param("chatRoomId") chatRoomId: Long,
        @Param("sequence") sequence: Int,
        @Param("limit") limit: Int,
    ): List<ChatMessage>
}
