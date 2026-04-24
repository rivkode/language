package com.learner.language.infrastructure.comment

import com.learner.language.domain.comment.DiaryComment
import com.learner.language.domain.comment.DiaryCommentWriter
import org.springframework.stereotype.Component

@Component
class DiaryCommentWriterImpl(
    private val diaryCommentRepository: DiaryCommentRepository,
) : DiaryCommentWriter {

    override fun save(comment: DiaryComment): DiaryComment = diaryCommentRepository.save(comment)

    override fun delete(comment: DiaryComment) {
        diaryCommentRepository.delete(comment)
    }

    override fun incrementLikeCount(commentId: Long) {
        diaryCommentRepository.incrementLikeCount(commentId)
    }

    override fun decrementLikeCount(commentId: Long) {
        diaryCommentRepository.decrementLikeCount(commentId)
    }
}
