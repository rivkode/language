package com.learner.language.infrastructure.comment

import com.learner.language.domain.comment.CommentLike
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CommentLikeRepository : JpaRepository<CommentLike, Long> {
    fun existsByCommentIdAndUserId(commentId: Long, userId: Long): Boolean

    @Query("SELECT l.commentId FROM CommentLike l WHERE l.commentId IN :commentIds AND l.userId = :userId")
    fun findLikedCommentIds(
        @Param("commentIds") commentIds: Collection<Long>,
        @Param("userId") userId: Long,
    ): List<Long>

    @Modifying
    @Query("DELETE FROM CommentLike l WHERE l.commentId = :commentId AND l.userId = :userId")
    fun deleteByCommentIdAndUserId(
        @Param("commentId") commentId: Long,
        @Param("userId") userId: Long,
    ): Int
}
