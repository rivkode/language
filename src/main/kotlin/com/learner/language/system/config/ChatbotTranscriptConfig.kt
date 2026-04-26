package com.learner.language.system.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestClient
import java.time.Duration

@Configuration
class ChatbotTranscriptConfig(
    @Value("\${app.chatbot.base-url}") private val baseUrl: String,
    @Value("\${app.chatbot.connect-timeout}") private val connectTimeout: Duration,
    @Value("\${app.chatbot.read-timeout}") private val readTimeout: Duration
) {
    @Bean
    fun chatbotRestClient(builder: RestClient.Builder): RestClient {
        val requestFactory = SimpleClientHttpRequestFactory().apply {
            setConnectTimeout(connectTimeout)
            setReadTimeout(readTimeout)
        }

        return builder
            .baseUrl(baseUrl)
            .requestFactory(requestFactory)
            .build()
    }
}
