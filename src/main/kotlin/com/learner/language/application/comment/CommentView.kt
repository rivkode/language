package com.learner.language.application.comment

import com.learner.language.application.diary.DiaryAuthorView
import java.time.Instant

data class DiaryCommentView(
    val commentId: Long,
    val diaryId: Long,
    val author: DiaryAuthorView,
    val text: String,
    val createdAt: Instant,
    val parentCommentId: Long?,
    val likeCount: Int,
    val userLiked: Boolean,
)
