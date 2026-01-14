package com.learner.language.system.config

import org.springframework.ai.chat.client.ChatClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AiChatConfig(
    val chatClient: ChatClient.Builder
) {
    @Bean
    fun chatClient(): ChatClient {
        return chatClient.build()
    }
}
