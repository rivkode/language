package com.learner.language.infrastructure.profile

import com.learner.language.domain.profile.UserProfile
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface UserProfileRepository : JpaRepository<UserProfile, Long> {
    fun findByUserId(userId: Long): Optional<UserProfile>
}
