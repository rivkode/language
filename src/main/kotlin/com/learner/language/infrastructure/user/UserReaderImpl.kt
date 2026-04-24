package com.learner.language.infrastructure.user

import com.learner.language.domain.user.AuthProvider
import com.learner.language.domain.user.User
import com.learner.language.domain.user.UserEmail
import com.learner.language.domain.user.UserReader
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Component

@Component
class UserReaderImpl(
    private val userRepository: UserRepository
) : UserReader {
    override fun getUserById(userId: Long): User {
        return userRepository.findById(userId)
            .orElseThrow {
                EntityNotFoundException()
            }
    }

    override fun getUserByEmail(email: String): User {
        val userEmail = UserEmail(email)
        return userRepository.findByEmail(userEmail)
            .orElseThrow {
                EntityNotFoundException()
            }
    }

    override fun findByEmail(email: String): User? {
        val userEmail = UserEmail(email)
        return userRepository.findByEmail(userEmail).orElse(null)
    }

    override fun findByProvider(provider: AuthProvider, externalId: String): User? {
        return userRepository.findByProviderAndProviderExternalId(provider, externalId).orElse(null)
    }

    override fun existsByEmail(userEmail: UserEmail): Boolean {
        return userRepository.existsByEmail(userEmail)
    }
}
