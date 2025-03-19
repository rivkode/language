package com.learner.language.system.security

import com.learner.language.domain.user.UserEmail
import com.learner.language.domain.user.exception.UserNotFoundException
import com.learner.language.infrastructure.user.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl(
    private val userRepository: UserRepository
) : UserDetailsService {

    fun getUserEmail(userId: Long): String {
        val user = userRepository.findById(userId)
            .orElseThrow { UserNotFoundException("존재하지 않는 유저입니다.") }
        return user.email.email
    }

    override fun loadUserByUsername(email: String): UserDetails {
        val userEmail = UserEmail(email)
        val user = userRepository.findByEmail(userEmail).orElseThrow()
        return UserDetailsImpl(user)
    }
}
