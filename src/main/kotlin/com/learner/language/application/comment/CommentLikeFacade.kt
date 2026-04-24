package com.learner.language.application.comment

import com.learner.language.domain.comment.CommentLike
import com.learner.language.domain.comment.DiaryCommentReader
import com.learner.language.domain.comment.DiaryCommentWriter
import com.learner.language.infrastructure.comment.CommentLikeRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CommentLikeFacade(
    private val commentReader: DiaryCommentReader,
    private val commentWriter: DiaryCommentWriter,
    private val commentLikeRepository: CommentLikeRepository,
) {

    data class Result(
        val commentId: Long,
        val likeCount: Int,
        val userLiked: Boolean,
    )

    @Transactional
    fun setLiked(commentId: Long, userId: Long, liked: Boolean): Result {
        val comment = commentReader.getById(commentId)
        val currentlyLiked = commentLikeRepository.existsByCommentIdAndUserId(commentId, userId)

        when {
            liked && !currentlyLiked -> {
                try {
                    commentLikeRepository.save(CommentLike(commentId = commentId, userId = userId))
                    commentWriter.incrementLikeCount(commentId)
                } catch (_: DataIntegrityViolationException) {
                    // 멱등 처리
                }
            }
            !liked && currentlyLiked -> {
                val removed = commentLikeRepository.deleteByCommentIdAndUserId(commentId, userId)
                if (removed > 0) commentWriter.decrementLikeCount(commentId)
            }
        }

        val updated = commentReader.getById(comment.id)
        return Result(
            commentId = updated.id,
            likeCount = updated.likeCount,
            userLiked = liked,
        )
    }
}
