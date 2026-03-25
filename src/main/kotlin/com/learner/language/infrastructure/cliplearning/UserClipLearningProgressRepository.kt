package com.learner.language.infrastructure.cliplearning

import com.learner.language.domain.cliplearning.UserClipLearningProgress
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface UserClipLearningProgressRepository : JpaRepository<UserClipLearningProgress, Long> {
    fun findByUserIdAndClipId(userId: Long, clipId: Long): Optional<UserClipLearningProgress>
    fun findAllByUserIdAndClipIdIn(userId: Long, clipIds: List<Long>): List<UserClipLearningProgress>
}
