package com.learner.language.application.diarychat

import com.fasterxml.jackson.databind.ObjectMapper
import com.learner.language.application.diary.DiaryAuthorView
import com.learner.language.domain.diarychat.DiaryChatMessage
import com.learner.language.domain.diarychat.DiaryChatMessageSource
import com.learner.language.domain.diarychat.DiaryChatReader
import org.springframework.stereotype.Component
import java.time.ZoneOffset

@Component
class DiaryChatPollViewAssembler(
    private val chatReader: DiaryChatReader,
    private val authorResolver: DiaryChatAuthorResolver,
    private val objectMapper: ObjectMapper,
) {

    fun pollForRoom(roomId: Long, after: Long): DiaryChatPollView {
        val messages = chatReader.findMessagesAfter(roomId, after)
        if (messages.isEmpty()) {
            return DiaryChatPollView(emptyList(), emptyList(), after)
        }

        val userIds = messages.mapNotNull { it.authorUserId }.distinct()
        val authors = if (userIds.isNotEmpty()) authorResolver.resolve(userIds) else emptyMap()
        val aiAuthor = authorResolver.aiAuthor()

        val items = mutableListOf<DiaryChatMessageView>()
        val events = mutableListOf<DiaryChatEventView>()

        messages.forEach { message ->
            when (message.source) {
                DiaryChatMessageSource.USER -> items += toMessageView(
                    message,
                    authors[message.authorUserId] ?: aiAuthor,
                )
                DiaryChatMessageSource.AI -> items += toMessageView(message, aiAuthor)
                DiaryChatMessageSource.SYSTEM -> toEventView(message)?.let { events += it }
            }
        }

        val nextAfter = messages.last().id
        return DiaryChatPollView(items, events, nextAfter)
    }

    fun messagesBefore(roomId: Long, before: Long?, size: Int): List<DiaryChatMessageView> {
        val messages = chatReader.findMessagesBefore(roomId, before, size)
            .filter { it.source != DiaryChatMessageSource.SYSTEM }
        val userIds = messages.mapNotNull { it.authorUserId }.distinct()
        val authors = if (userIds.isNotEmpty()) authorResolver.resolve(userIds) else emptyMap()
        val aiAuthor = authorResolver.aiAuthor()
        return messages
            .sortedBy { it.id }
            .map { toMessageView(it, authors[it.authorUserId] ?: aiAuthor) }
    }

    fun toMessageView(message: DiaryChatMessage, author: DiaryAuthorView): DiaryChatMessageView =
        DiaryChatMessageView(
            messageId = message.id,
            roomId = message.roomId,
            author = author,
            text = message.text,
            audioUrl = message.audioUrl,
            createdAt = message.createdAt.toInstant(ZoneOffset.UTC),
            source = message.source.apiValue(),
        )

    private fun toEventView(message: DiaryChatMessage): DiaryChatEventView? {
        val eventType = message.eventType ?: return null
        val payload = runCatching {
            @Suppress("UNCHECKED_CAST")
            objectMapper.readValue(message.text, Map::class.java) as Map<String, Any?>
        }.getOrElse { emptyMap() }

        return DiaryChatEventView(
            type = eventType.apiValue(),
            userId = (payload["userId"] as? Number)?.toLong(),
            enabled = payload["enabled"] as? Boolean,
            at = message.createdAt.toInstant(ZoneOffset.UTC),
        )
    }
}
