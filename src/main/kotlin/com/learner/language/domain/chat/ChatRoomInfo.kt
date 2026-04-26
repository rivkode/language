package com.learner.language.domain.chat

class ChatRoomInfo(
    val chatRoomId: Long,
    val name: String,
    val personaType: String,
    val contextType: String,
    val youtubeVideoId: String?,
    val lastMessageDateTime: String
) {
    constructor(chatRoom: ChatRoom): this(
        chatRoomId = chatRoom.id,
        name = chatRoom.name,
        personaType = chatRoom.personaType.name,
        contextType = chatRoom.contextType.name,
        youtubeVideoId = chatRoom.videoId,
        lastMessageDateTime = chatRoom.lastMessageDateTime.toString()
    )

    companion object{
        fun from(chatRooms: List<ChatRoom>): List<ChatRoomInfo> {
            return chatRooms.map { ChatRoomInfo(it) }
        }
    }
}
