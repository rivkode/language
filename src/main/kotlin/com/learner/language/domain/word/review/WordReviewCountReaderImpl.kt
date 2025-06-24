package com.learner.language.domain.word.review

import com.learner.language.infrastructure.word.review.WordReviewCountRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class WordReviewCountReaderImpl(
    private val wordReviewCountRepository: WordReviewCountRepository
) {
    @Transactional
    fun getWordReviewCount(userId: Long, wordId: Long): WordReviewCount? {
        return wordReviewCountRepository.getByUserIdAndWordId(userId, wordId)
    }
}