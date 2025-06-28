package com.learner.language.domain.chat

interface ChatWriter {
    fun save(chatMessage: ChatMessage) : ChatMessage
}