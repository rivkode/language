package com.learner.language.infrastructure.chat

import com.learner.language.domain.chat.ChatMessage
import com.learner.language.domain.chat.ChatReader
import com.learner.language.domain.chat.ChatRoom
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Component

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
        return chatRepository.findLastMessage(chatRoomId)
    }

    override fun getChatMessageById(chatId: Long): ChatMessage {
        return chatRepository.findById(chatId).orElseThrow()
    }

    override fun getPreviousChatMessages(chatRoomId: Long, sequence: Int, limit: Int): List<ChatMessage> {
        return chatRepository.findPreviousMessages(chatRoomId, sequence, limit)
            .sortedBy { it.sequence }
    }
}
