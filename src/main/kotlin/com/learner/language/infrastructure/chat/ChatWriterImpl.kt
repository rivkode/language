package com.learner.language.infrastructure.chat

import com.learner.language.domain.chat.ChatMessage
import com.learner.language.domain.chat.ChatWriter
import org.springframework.stereotype.Component

@Component
class ChatWriterImpl(
    private val chatRepository: ChatRepository
) : ChatWriter {
    override fun save(chatMessage: ChatMessage): ChatMessage {
        return chatRepository.save(chatMessage)
    }
}