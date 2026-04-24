package com.learner.language.domain.comment

interface DiaryCommentWriter {
    fun save(comment: DiaryComment): DiaryComment
    fun delete(comment: DiaryComment)
    fun incrementLikeCount(commentId: Long)
    fun decrementLikeCount(commentId: Long)
}
