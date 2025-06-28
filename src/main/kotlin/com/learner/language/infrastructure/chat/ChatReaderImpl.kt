package com.learner.language.infrastructure.chat

import com.learner.language.domain.chat.ChatMessage
import com.learner.language.domain.chat.ChatReader
import com.learner.language.domain.chat.ChatRoom
import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.Predicate
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ChatReaderImpl(
    private val chatRepository: ChatRepository,
    private val entityManager: EntityManager
) : ChatReader {
    override fun getChatMessageListByChatRoomId(chatRoomId: Long): List<ChatMessage> {
        val cb = entityManager.criteriaBuilder
        val query = cb.createQuery(ChatMessage::class.java)
        val root = query.from(ChatMessage::class.java)
        val chatRoomJoin = root.join<ChatRoom, ChatMessage>("chatRoom")


        query.select(root).where(
            cb.equal(chatRoomJoin.get<Long>("id"), chatRoomId)
        ).orderBy(cb.asc(root.get<Int>("sequence")))

        val typedQuery = entityManager.createQuery(query)

        return typedQuery.resultList
    }

    override fun getLastChatMessageByChatRoomId(chatRoomId: Long): ChatMessage? {
        val cb = entityManager.criteriaBuilder
        val query = cb.createQuery(ChatMessage::class.java)
        val root = query.from(ChatMessage::class.java)
        val predicates = mutableListOf<Predicate>()
        predicates.add(cb.equal(root.get<ChatRoom>("chatRoom").get<Long>("id"), chatRoomId))

        query.select(root).where(
            *predicates.toTypedArray()
        ).orderBy(cb.desc(root.get<LocalDateTime>("createdAt")))

        val typedQuery = entityManager.createQuery(query)
        typedQuery.maxResults = 1
        val results = typedQuery.resultList

        return results.firstOrNull()
    }
}