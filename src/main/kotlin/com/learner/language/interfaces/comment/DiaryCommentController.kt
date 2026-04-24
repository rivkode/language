package com.learner.language.interfaces.comment

import com.learner.language.application.comment.DiaryCommentFacade
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
@RequestMapping("/api/v1/diaries/{diaryId}/comments")
class DiaryCommentController(
    private val commentFacade: DiaryCommentFacade,
) {

    @GetMapping
    fun list(
        @LoginUser userId: Long,
        @PathVariable diaryId: Long,
        @RequestParam(required = false) cursor: String?,
        @RequestParam(required = false, defaultValue = "20") size: Int,
    ): ResponseEntity<CommentDto.CommentListResponse> {
        val page = commentFacade.list(diaryId, cursor, size, userId)
        return ResponseEntity.ok(CommentDto.CommentListResponse.from(page))
    }

    @PostMapping
    fun create(
        @LoginUser userId: Long,
        @PathVariable diaryId: Long,
        @Valid @RequestBody request: CommentDto.CreateRequest,
    ): ResponseEntity<CommentDto.CommentResponse> {
        val view = commentFacade.create(request.toCommand(diaryId, userId))
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(CommentDto.CommentResponse.from(view))
    }
}
