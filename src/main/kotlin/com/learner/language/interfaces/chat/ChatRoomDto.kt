package com.learner.language.interfaces.chat

import com.learner.language.domain.chat.ChatRoomInfo

class ChatRoomDto {
    data class Response(
        val chatRoomListInfo: List<ChatRoomInfo>
    )

}
