package com.learner.language.application.diary

import com.learner.language.domain.diary.DiaryLike
import com.learner.language.domain.diary.DiaryReader
import com.learner.language.domain.diary.DiaryWriter
import com.learner.language.infrastructure.diary.DiaryLikeRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DiaryLikeFacade(
    private val diaryReader: DiaryReader,
    private val diaryWriter: DiaryWriter,
    private val diaryLikeRepository: DiaryLikeRepository,
) {

    data class Result(
        val diaryId: Long,
        val likeCount: Int,
        val userLiked: Boolean,
    )

    @Transactional
    fun setLiked(diaryId: Long, userId: Long, liked: Boolean): Result {
        val diary = diaryReader.getById(diaryId)
        val currentlyLiked = diaryLikeRepository.existsByDiaryIdAndUserId(diaryId, userId)

        when {
            liked && !currentlyLiked -> {
                try {
                    diaryLikeRepository.save(DiaryLike(diaryId = diaryId, userId = userId))
                    diaryWriter.incrementLikeCount(diaryId)
                } catch (_: DataIntegrityViolationException) {
                    // 동시에 이미 좋아요가 들어간 경우: 멱등하므로 무시
                }
            }
            !liked && currentlyLiked -> {
                val removed = diaryLikeRepository.deleteByDiaryIdAndUserId(diaryId, userId)
                if (removed > 0) diaryWriter.decrementLikeCount(diaryId)
            }
        }

        val updated = diaryReader.getById(diary.id)
        return Result(
            diaryId = updated.id,
            likeCount = updated.likeCount,
            userLiked = liked,
        )
    }
}
