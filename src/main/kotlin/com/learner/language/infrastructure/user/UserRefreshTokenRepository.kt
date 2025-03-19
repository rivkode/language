package com.learner.language.infrastructure.user

import com.learner.language.domain.user.RefreshToken
import com.learner.language.domain.user.UserRefreshToken
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserRefreshTokenRepository: CrudRepository<UserRefreshToken, Long> {
    fun findByUserId(userId: Long): Optional<UserRefreshToken>

    fun findByRefreshTokenRefreshToken(refreshToken: String): Optional<UserRefreshToken>
}
