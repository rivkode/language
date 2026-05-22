package com.learner.language.infrastructure.shorts

import com.learner.language.domain.shorts.UserSavedShort
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface UserSavedShortRepository : JpaRepository<UserSavedShort, Long> {
    fun findByUserIdAndShortId(userId: Long, shortId: Long): Optional<UserSavedShort>
    fun findAllByUserIdAndShortIdIn(userId: Long, shortIds: List<Long>): List<UserSavedShort>
    fun existsByUserIdAndShortId(userId: Long, shortId: Long): Boolean
    fun deleteByUserIdAndShortId(userId: Long, shortId: Long)
}
