package com.learner.language.infrastructure.diary

import com.learner.language.domain.diary.DiaryLikeReader
import org.springframework.stereotype.Component

@Component
class DiaryLikeReaderImpl(
    private val diaryLikeRepository: DiaryLikeRepository,
) : DiaryLikeReader {

    override fun isLikedBy(diaryId: Long, userId: Long): Boolean =
        diaryLikeRepository.existsByDiaryIdAndUserId(diaryId, userId)

    override fun findLikedDiaryIds(diaryIds: Collection<Long>, userId: Long): Set<Long> {
        if (diaryIds.isEmpty()) return emptySet()
        return diaryLikeRepository.findLikedDiaryIds(diaryIds, userId).toSet()
    }
}
