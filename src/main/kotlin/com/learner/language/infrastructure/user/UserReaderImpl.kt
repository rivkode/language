package com.learner.language.infrastructure.user

import com.learner.language.domain.user.User
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
        return userRepository.findByEmail(email)
            .orElseThrow {
                EntityNotFoundException()
            }
    }
}
