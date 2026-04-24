package com.learner.language.infrastructure.diary

import com.learner.language.domain.diary.DiaryLike
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface DiaryLikeRepository : JpaRepository<DiaryLike, Long> {
    fun existsByDiaryIdAndUserId(diaryId: Long, userId: Long): Boolean

    @Query("SELECT l.diaryId FROM DiaryLike l WHERE l.diaryId IN :diaryIds AND l.userId = :userId")
    fun findLikedDiaryIds(
        @Param("diaryIds") diaryIds: Collection<Long>,
        @Param("userId") userId: Long,
    ): List<Long>

    @Modifying
    @Query("DELETE FROM DiaryLike l WHERE l.diaryId = :diaryId AND l.userId = :userId")
    fun deleteByDiaryIdAndUserId(
        @Param("diaryId") diaryId: Long,
        @Param("userId") userId: Long,
    ): Int
}
