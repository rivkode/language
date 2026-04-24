package com.learner.language.interfaces.comment

import com.learner.language.application.comment.CommentLikeFacade
import com.learner.language.application.comment.DiaryCommentFacade
import com.learner.language.system.login.LoginUser
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/comments")
class CommentController(
    private val commentFacade: DiaryCommentFacade,
    private val commentLikeFacade: CommentLikeFacade,
) {

    @PostMapping("/{commentId}/like")
    fun toggleLike(
        @LoginUser userId: Long,
        @PathVariable commentId: Long,
        @Valid @RequestBody request: CommentDto.LikeToggleRequest,
    ): ResponseEntity<CommentDto.LikeToggleResponse> {
        val result = commentLikeFacade.setLiked(commentId, userId, request.liked)
        return ResponseEntity.ok(CommentDto.LikeToggleResponse.from(result))
    }

    @DeleteMapping("/{commentId}")
    fun delete(
        @LoginUser userId: Long,
        @PathVariable commentId: Long,
    ): ResponseEntity<Void> {
        commentFacade.delete(commentId, userId)
        return ResponseEntity.noContent().build()
    }
}
