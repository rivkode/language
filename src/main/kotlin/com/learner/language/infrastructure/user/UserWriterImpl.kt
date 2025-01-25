package com.learner.language.infrastructure.user

import com.learner.language.domain.user.User
import com.learner.language.domain.user.UserWriter
import org.springframework.stereotype.Component

@Component
class UserWriterImpl(
    private val userRepository: UserRepository
) : UserWriter {
    override fun registerUser(user: User): User {
        return userRepository.save(user)
    }

    override fun updateUser(user: User) : User {
        return userRepository.save(user)
    }
}
