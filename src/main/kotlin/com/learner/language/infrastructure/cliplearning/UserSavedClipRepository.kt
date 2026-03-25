package com.learner.language.infrastructure.cliplearning

import com.learner.language.domain.cliplearning.UserSavedClip
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface UserSavedClipRepository : JpaRepository<UserSavedClip, Long> {
    fun findByUserIdAndClipId(userId: Long, clipId: Long): Optional<UserSavedClip>
    fun findAllByUserIdAndClipIdIn(userId: Long, clipIds: List<Long>): List<UserSavedClip>
    fun countByUserId(userId: Long): Long
    fun existsByUserIdAndClipId(userId: Long, clipId: Long): Boolean
    fun deleteByUserIdAndClipId(userId: Long, clipId: Long)
}
