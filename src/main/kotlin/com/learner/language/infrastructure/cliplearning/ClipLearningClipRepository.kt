package com.learner.language.infrastructure.cliplearning

import com.learner.language.domain.cliplearning.ClipLearningClip
import org.springframework.data.jpa.repository.JpaRepository

interface ClipLearningClipRepository : JpaRepository<ClipLearningClip, Long> {
    fun findAllByCategoryOrderByIdDesc(category: String): List<ClipLearningClip>
}
