package com.learner.language.interfaces.diarychat

import com.learner.language.application.diarychat.CreateChatRoomCommand
import com.learner.language.application.diarychat.DiaryChatRoomFacade
import com.learner.language.system.login.LoginUser
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/chatrooms")
class DiaryChatRoomController(
    private val facade: DiaryChatRoomFacade,
) {

    @PostMapping
    fun create(
        @LoginUser userId: Long,
        @Valid @RequestBody request: DiaryChatDto.CreateChatRoomRequest,
    ): ResponseEntity<DiaryChatDto.ChatRoomResponse> {
        val view = facade.createOrGet(
            CreateChatRoomCommand(
                diaryId = request.diaryId,
                aiAssistantEnabled = request.aiAssistantEnabled,
                requesterUserId = userId,
            )
        )
        return ResponseEntity.ok(DiaryChatDto.ChatRoomResponse.from(view))
    }

    @GetMapping("/{roomId}")
    fun get(
        @LoginUser userId: Long,
        @PathVariable roomId: Long,
    ): ResponseEntity<DiaryChatDto.ChatRoomResponse> =
        ResponseEntity.ok(DiaryChatDto.ChatRoomResponse.from(facade.get(roomId)))

    @GetMapping("/{roomId}/participants")
    fun participants(
        @LoginUser userId: Long,
        @PathVariable roomId: Long,
    ): ResponseEntity<DiaryChatDto.ParticipantListResponse> {
        val items = facade.getParticipants(roomId).map { DiaryChatDto.ParticipantResponse.from(it) }
        return ResponseEntity.ok(DiaryChatDto.ParticipantListResponse(items))
    }

    @PostMapping("/{roomId}/join")
    fun join(
        @LoginUser userId: Long,
        @PathVariable roomId: Long,
    ): ResponseEntity<DiaryChatDto.ChatRoomResponse> =
        ResponseEntity.ok(DiaryChatDto.ChatRoomResponse.from(facade.join(roomId, userId)))

    @PostMapping("/{roomId}/leave")
    fun leave(
        @LoginUser userId: Long,
        @PathVariable roomId: Long,
    ): ResponseEntity<Void> {
        facade.leave(roomId, userId)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{roomId}/ai-toggle")
    fun aiToggle(
        @LoginUser userId: Long,
        @PathVariable roomId: Long,
        @Valid @RequestBody request: DiaryChatDto.AiToggleRequest,
    ): ResponseEntity<DiaryChatDto.ChatRoomResponse> =
        ResponseEntity.ok(DiaryChatDto.ChatRoomResponse.from(facade.setAiAssistant(roomId, userId, request.enabled)))
}
