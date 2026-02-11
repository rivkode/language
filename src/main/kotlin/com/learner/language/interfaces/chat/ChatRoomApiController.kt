package com.learner.language.interfaces.chat

import com.learner.language.application.chat.ChatFacade
import com.learner.language.system.login.LoginUser
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/chatrooms")
class ChatRoomApiController(
    private val chatFacade: ChatFacade
) {
    @GetMapping
    fun retrieveChatRoomList(
        @LoginUser userId: Long
    ): ResponseEntity<ChatRoomDto.Response> {
        val chatRoomListInfo = chatFacade.retrieveChatRoom(userId)
        val response = ChatRoomDto.Response(chatRoomListInfo)

        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @PostMapping
    fun registerChatRoom(
        @LoginUser userId: Long,
        @RequestBody chatRoomRegisterRequest: ChatRoomDto.RegisterRequest
    ): ResponseEntity<ChatRoomDto.RegisterResponse> {
        val command = chatRoomRegisterRequest.toCommand()
        val chatRoomInfo = chatFacade.registerChatRoom(userId, command)
        val response = ChatRoomDto.RegisterResponse(chatRoomInfo)

        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/{chatRoomId}/chat")
    fun retrieveChatList(
        @LoginUser userId: Long,
        @PathVariable("chatRoomId") chatRoomId: Long
    ): ResponseEntity<ChatDto.ChatListResponse> {
        val chatListInfo = chatFacade.retrieveChat(userId, chatRoomId)
        val response = ChatDto.ChatListResponse(chatListInfo)

        return ResponseEntity.status(HttpStatus.OK).body(response)
    }
}
