package com.learner.language.infrastructure.cliplearning

import com.learner.language.domain.cliplearning.ClipLearningVocabulary
import org.springframework.data.jpa.repository.JpaRepository

interface ClipLearningVocabularyRepository : JpaRepository<ClipLearningVocabulary, Long> {
    fun findAllByClipIdOrderByDisplayOrderAsc(clipId: Long): List<ClipLearningVocabulary>
    fun findAllByClipIdInOrderByClipIdAscDisplayOrderAsc(clipIds: List<Long>): List<ClipLearningVocabulary>
}
