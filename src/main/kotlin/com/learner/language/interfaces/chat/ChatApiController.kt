package com.learner.language.interfaces.chat

import com.learner.language.application.chat.ChatFacade
import com.learner.language.system.login.LoginUser
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/chat")
class ChatApiController(
    private val chatFacade: ChatFacade
) {
    @PostMapping
    fun registerChat(
        @LoginUser userId: Long,
        @Valid @RequestBody registerRequest: ChatDto.RegisterRequest
    ): ResponseEntity<ChatDto.RegisterResponse> {
        val command = registerRequest.toCommand()
        val chatInfo = chatFacade.registerChat(command, userId)
        val response = ChatDto.RegisterResponse(chatInfo)

        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PostMapping("/generate")
    fun generateChat(
        @LoginUser userId: Long,
        @Valid @RequestBody generateRequest: ChatDto.GenerateRequest
    ): ResponseEntity<ChatDto.RegisterResponse> {
        val command = generateRequest.toCommand()
        val chatInfo = chatFacade.generateChat(command, userId)
        val response = ChatDto.RegisterResponse(chatInfo)

        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

}
