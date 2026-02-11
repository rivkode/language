package com.learner.language.infrastructure.chat

import com.learner.language.domain.chat.ChatRoom
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ChatRoomRepository : JpaRepository<ChatRoom, Long> {

    @Query(value = "SELECT * FROM chat_room WHERE user_id = :userId", nativeQuery = true)
    fun findByUserId(@Param("userId") userId: Long): List<ChatRoom>

    @Query(value = "SELECT * FROM chat_room WHERE user_id = :userId ORDER BY last_message_date_time DESC", nativeQuery = true)
    fun findByUserIdAndLastMessageDateTimeDesc(@Param("userId") userId: Long): List<ChatRoom>

    @Query(value = "SELECT * FROM chat_room WHERE user_id = :userId AND persona_type = :personaType", nativeQuery = true)
    fun findByUserIdAndPersonaType(@Param("userId") userId: Long, @Param("personaType") personaType: Int): ChatRoom
}
