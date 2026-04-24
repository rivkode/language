package com.learner.language.infrastructure.diary

import com.learner.language.domain.diary.Diary
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface DiaryRepository : JpaRepository<Diary, Long> {

    @Query(
        """
        SELECT d FROM Diary d
        WHERE d.isPublic = true
          AND (:cursorCreatedAt IS NULL
               OR d.createdAt < :cursorCreatedAt
               OR (d.createdAt = :cursorCreatedAt AND d.id < :cursorId))
        ORDER BY d.createdAt DESC, d.id DESC
        """
    )
    fun findPublicFeedRecent(
        @Param("cursorCreatedAt") cursorCreatedAt: LocalDateTime?,
        @Param("cursorId") cursorId: Long?,
        pageable: Pageable,
    ): List<Diary>

    @Query(
        """
        SELECT d FROM Diary d
        WHERE d.isPublic = true
          AND (:cursorLikeCount IS NULL
               OR d.likeCount < :cursorLikeCount
               OR (d.likeCount = :cursorLikeCount AND d.id < :cursorId))
        ORDER BY d.likeCount DESC, d.id DESC
        """
    )
    fun findPublicFeedTrending(
        @Param("cursorLikeCount") cursorLikeCount: Int?,
        @Param("cursorId") cursorId: Long?,
        pageable: Pageable,
    ): List<Diary>

    @Query(
        """
        SELECT d FROM Diary d
        WHERE d.isPublic = true
          AND d.id IN (SELECT t.diaryId FROM DiaryTag t WHERE t.tag = :tag)
          AND (:cursorCreatedAt IS NULL
               OR d.createdAt < :cursorCreatedAt
               OR (d.createdAt = :cursorCreatedAt AND d.id < :cursorId))
        ORDER BY d.createdAt DESC, d.id DESC
        """
    )
    fun findPublicFeedByTagRecent(
        @Param("tag") tag: String,
        @Param("cursorCreatedAt") cursorCreatedAt: LocalDateTime?,
        @Param("cursorId") cursorId: Long?,
        pageable: Pageable,
    ): List<Diary>

    @Query(
        """
        SELECT d FROM Diary d
        WHERE d.userId = :userId
          AND (:cursorCreatedAt IS NULL
               OR d.createdAt < :cursorCreatedAt
               OR (d.createdAt = :cursorCreatedAt AND d.id < :cursorId))
        ORDER BY d.createdAt DESC, d.id DESC
        """
    )
    fun findUserFeedRecent(
        @Param("userId") userId: Long,
        @Param("cursorCreatedAt") cursorCreatedAt: LocalDateTime?,
        @Param("cursorId") cursorId: Long?,
        pageable: Pageable,
    ): List<Diary>

    fun countByUserIdAndIsPublic(userId: Long, isPublic: Boolean): Long

    @Modifying
    @Query("UPDATE Diary d SET d.likeCount = d.likeCount + 1 WHERE d.id = :id")
    fun incrementLikeCount(@Param("id") id: Long): Int

    @Modifying
    @Query("UPDATE Diary d SET d.likeCount = d.likeCount - 1 WHERE d.id = :id AND d.likeCount > 0")
    fun decrementLikeCount(@Param("id") id: Long): Int

    @Modifying
    @Query("UPDATE Diary d SET d.commentCount = d.commentCount + 1 WHERE d.id = :id")
    fun incrementCommentCount(@Param("id") id: Long): Int

    @Modifying
    @Query("UPDATE Diary d SET d.commentCount = d.commentCount - 1 WHERE d.id = :id AND d.commentCount > 0")
    fun decrementCommentCount(@Param("id") id: Long): Int
}
