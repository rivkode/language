package com.learner.language.domain.diary

interface DiaryLikeReader {
    fun isLikedBy(diaryId: Long, userId: Long): Boolean
    fun findLikedDiaryIds(diaryIds: Collection<Long>, userId: Long): Set<Long>
}
