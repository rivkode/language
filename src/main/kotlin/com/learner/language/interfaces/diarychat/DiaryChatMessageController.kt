package com.learner.language.interfaces.diarychat

import com.learner.language.application.diarychat.DiaryChatMessageFacade
import com.learner.language.application.diarychat.SendMessageCommand
import com.learner.language.system.login.LoginUser
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/chatrooms/{roomId}/messages")
class DiaryChatMessageController(
    private val facade: DiaryChatMessageFacade,
) {

    @GetMapping
    fun history(
        @LoginUser userId: Long,
        @PathVariable roomId: Long,
        @RequestParam(required = false) before: Long?,
        @RequestParam(required = false, defaultValue = "30") size: Int,
    ): ResponseEntity<DiaryChatDto.MessageHistoryResponse> {
        val result = facade.history(roomId, userId, before, size)
        return ResponseEntity.ok(
            DiaryChatDto.MessageHistoryResponse(
                items = result.items.map { DiaryChatDto.ChatMessageResponse.from(it) },
                hasMore = result.hasMore,
                oldestMessageId = result.oldestMessageId,
            )
        )
    }

    @PostMapping
    fun send(
        @LoginUser userId: Long,
        @PathVariable roomId: Long,
        @Valid @RequestBody request: DiaryChatDto.SendMessageRequest,
    ): ResponseEntity<DiaryChatDto.ChatMessageResponse> {
        val view = facade.send(
            SendMessageCommand(
                roomId = roomId,
                authorUserId = userId,
                text = request.text,
                audioUrl = request.audioUrl,
            )
        )
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(DiaryChatDto.ChatMessageResponse.from(view))
    }
}
