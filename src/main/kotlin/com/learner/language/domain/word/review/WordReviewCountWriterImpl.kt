package com.learner.language.domain.word.review

import com.learner.language.infrastructure.word.review.WordReviewCountRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class WordReviewCountWriterImpl(
    private val wordReviewCountRepository: WordReviewCountRepository
) {

    @Transactional
    fun save(wordReviewCount: WordReviewCount) {
        wordReviewCountRepository.save(wordReviewCount)
    }
}
