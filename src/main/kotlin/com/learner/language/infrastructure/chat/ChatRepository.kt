package com.learner.language.infrastructure.chat

import com.learner.language.domain.chat.ChatMessage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatRepository : JpaRepository<ChatMessage, Long> {
}