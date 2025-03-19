package com.learner.language.system.config

import org.springframework.ai.autoconfigure.openai.OpenAiEmbeddingProperties
import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.ai.openai.OpenAiEmbeddingModel
import org.springframework.ai.openai.api.OpenAiApi
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(OpenAiEmbeddingProperties::class)
class OpenAiEmbeddingConfig(
    private val openAiEmbeddingProperties: OpenAiEmbeddingProperties
) {
    @Bean
    fun embeddingModel(): EmbeddingModel {
        return OpenAiEmbeddingModel(OpenAiApi(openAiEmbeddingProperties.apiKey))
    }
}
