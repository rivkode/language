package com.learner.language.application.comment

data class CreateCommentCommand(
    val diaryId: Long,
    val authorUserId: Long,
    val text: String,
    val parentCommentId: Long?,
)
