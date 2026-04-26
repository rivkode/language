package com.learner.language.infrastructure.cliplearning

import com.fasterxml.jackson.annotation.JsonProperty
import com.learner.language.domain.cliplearning.ClipLearningTranscriptInfo
import com.learner.language.domain.cliplearning.ClipLearningTranscriptItemInfo
import com.learner.language.domain.cliplearning.ClipLearningTranscriptReader
import com.learner.language.system.exception.ErrorCode
import com.learner.language.system.exception.ServiceUnavailableException
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestClientResponseException

@Component
class ChatbotTranscriptClient(
    @Qualifier("chatbotRestClient") private val restClient: RestClient,
    @Value("\${app.chatbot.transcript-path}") private val transcriptPath: String,
) : ClipLearningTranscriptReader {

    override fun retrieveTranscript(videoId: String): ClipLearningTranscriptInfo {
        val response = try {
            restClient.get()
                .uri { uriBuilder ->
                    uriBuilder
                        .path(transcriptPath)
                        .queryParam("video_id", videoId)
                        .build()
                }
                .retrieve()
                .body(ChatbotTranscriptResponse::class.java)
                ?: throw ServiceUnavailableException(
                    ErrorCode.SERVICE_UNAVAILABLE,
                    "chatbot-service transcript response is empty"
                )
        } catch (e: RestClientResponseException) {
            throw ServiceUnavailableException(
                ErrorCode.SERVICE_UNAVAILABLE,
                "chatbot-service transcript request failed with status=${e.statusCode.value()}"
            )
        } catch (e: ResourceAccessException) {
            throw ServiceUnavailableException(
                ErrorCode.SERVICE_UNAVAILABLE,
                "chatbot-service transcript request timed out or could not connect"
            )
        } catch (e: RestClientException) {
            throw ServiceUnavailableException(
                ErrorCode.SERVICE_UNAVAILABLE,
                "chatbot-service transcript request failed"
            )
        }

        return response.toInfo()
    }

    private fun ChatbotTranscriptResponse.toInfo(): ClipLearningTranscriptInfo {
        return ClipLearningTranscriptInfo(
            videoId = videoId,
            languagePriority = languagePriority,
            count = count,
            items = items.map {
                ClipLearningTranscriptItemInfo(
                    text = it.text,
                    start = it.start,
                    duration = it.duration
                )
            }
        )
    }
}

private data class ChatbotTranscriptResponse(
    @JsonProperty("video_id")
    val videoId: String,
    @JsonProperty("language_priority")
    val languagePriority: List<String>,
    val count: Int,
    val items: List<ChatbotTranscriptItemResponse>,
)

private data class ChatbotTranscriptItemResponse(
    val text: String,
    val start: Double,
    val duration: Double,
)
