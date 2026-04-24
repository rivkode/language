package com.learner.language.interfaces.comment

import com.learner.language.application.comment.CommentLikeFacade
import com.learner.language.application.comment.CreateCommentCommand
import com.learner.language.application.comment.DiaryCommentView
import com.learner.language.common.pagination.CursorPage
import com.learner.language.common.pagination.PageInfo
import com.learner.language.interfaces.diary.DiaryDto
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.Instant

object CommentDto {

    data class CreateRequest(
        @field:NotBlank
        val text: String,
        val parentCommentId: Long? = null,
    ) {
        fun toCommand(diaryId: Long, authorUserId: Long) = CreateCommentCommand(
            diaryId = diaryId,
            authorUserId = authorUserId,
            text = text,
            parentCommentId = parentCommentId,
        )
    }

    data class CommentResponse(
        val commentId: Long,
        val diaryId: Long,
        val author: DiaryDto.AuthorResponse,
        val text: String,
        val createdAt: Instant,
        val parentCommentId: Long?,
        val likeCount: Int,
        val userLiked: Boolean,
    ) {
        companion object {
            fun from(view: DiaryCommentView) = CommentResponse(
                commentId = view.commentId,
                diaryId = view.diaryId,
                author = DiaryDto.AuthorResponse.from(view.author),
                text = view.text,
                createdAt = view.createdAt,
                parentCommentId = view.parentCommentId,
                likeCount = view.likeCount,
                userLiked = view.userLiked,
            )
        }
    }

    data class CommentListResponse(
        val items: List<CommentResponse>,
        val paging: PageInfo,
    ) {
        companion object {
            fun from(page: CursorPage<DiaryCommentView>) = CommentListResponse(
                items = page.items.map { CommentResponse.from(it) },
                paging = page.paging,
            )
        }
    }

    data class LikeToggleRequest(
        @field:NotNull
        val liked: Boolean,
    )

    data class LikeToggleResponse(
        val commentId: Long,
        val likeCount: Int,
        val userLiked: Boolean,
    ) {
        companion object {
            fun from(result: CommentLikeFacade.Result) = LikeToggleResponse(
                commentId = result.commentId,
                likeCount = result.likeCount,
                userLiked = result.userLiked,
            )
        }
    }
}
