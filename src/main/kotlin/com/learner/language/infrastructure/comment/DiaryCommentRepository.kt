package com.learner.language.infrastructure.comment

import com.learner.language.domain.comment.DiaryComment
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface DiaryCommentRepository : JpaRepository<DiaryComment, Long> {

    @Query(
        """
        SELECT c FROM DiaryComment c
        WHERE c.diaryId = :diaryId
          AND (:cursorCreatedAt IS NULL
               OR c.createdAt > :cursorCreatedAt
               OR (c.createdAt = :cursorCreatedAt AND c.id > :cursorId))
        ORDER BY c.createdAt ASC, c.id ASC
        """
    )
    fun findByDiaryIdFromCursor(
        @Param("diaryId") diaryId: Long,
        @Param("cursorCreatedAt") cursorCreatedAt: LocalDateTime?,
        @Param("cursorId") cursorId: Long?,
        pageable: Pageable,
    ): List<DiaryComment>

    fun existsByIdAndDiaryId(id: Long, diaryId: Long): Boolean

    @Modifying
    @Query("UPDATE DiaryComment c SET c.likeCount = c.likeCount + 1 WHERE c.id = :id")
    fun incrementLikeCount(@Param("id") id: Long): Int

    @Modifying
    @Query("UPDATE DiaryComment c SET c.likeCount = c.likeCount - 1 WHERE c.id = :id AND c.likeCount > 0")
    fun decrementLikeCount(@Param("id") id: Long): Int
}
