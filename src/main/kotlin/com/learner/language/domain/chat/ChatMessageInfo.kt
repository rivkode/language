package com.learner.language.domain.chat

data class ChatMessageInfo(
    val userId: Long,
    val chatMessageId: Long,
    val chatRoomId: Long,
    val message: String,
    val sequence: Int,
    val senderType: SenderType,
) {
    constructor(chatMessage: ChatMessage): this(
        userId = chatMessage.user.id,
        chatMessageId = chatMessage.id,
        chatRoomId = chatMessage.chatRoom.id,
        message = chatMessage.message,
        sequence = chatMessage.sequence,
        senderType = chatMessage.senderType
    )

    companion object {
        fun from(chatMessageList: List<ChatMessage>): List<ChatMessageInfo> {
            return chatMessageList.map {
                ChatMessageInfo(it)
            }
        }
    }



}