package com.learner.language.infrastructure.diary

import com.learner.language.domain.diary.DiaryTag
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface DiaryTagRepository : JpaRepository<DiaryTag, Long> {
    fun findByDiaryId(diaryId: Long): List<DiaryTag>
    fun findByDiaryIdIn(diaryIds: Collection<Long>): List<DiaryTag>

    @Modifying
    @Query("DELETE FROM DiaryTag t WHERE t.diaryId = :diaryId")
    fun deleteByDiaryId(@Param("diaryId") diaryId: Long): Int
}
