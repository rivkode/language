package com.learner.language.domain.user

import com.learner.language.domain.user.exception.UserBadRequestException
import com.learner.language.system.security.CustomPasswordEncoder
import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class UserPassword(
    rawPassword: String,
    passwordEncoder: CustomPasswordEncoder
) {
    @Column(name = "password")
    val password: String = passwordEncoder.encodePassword(rawPassword).also {
        validateNotNull(rawPassword)
        validateUserPasswordLength(rawPassword)
    }

    companion object {
        private const val MIN_PASSWORD_LENGTH = 8
        private const val MAX_PASSWORD_LENGTH = 18
    }

    private fun validateNotNull(password: String) {
        if (password.isEmpty()) {
            throw UserBadRequestException("password 는 필수 값 입니다.")
        }
    }

    private fun validateUserPasswordLength(password: String) {
        if (password.length !in MIN_PASSWORD_LENGTH..MAX_PASSWORD_LENGTH) {
            throw UserBadRequestException(
                "패스워드는 $MIN_PASSWORD_LENGTH 자 이상, $MAX_PASSWORD_LENGTH 자 이하여야 합니다."
            )
        }
    }
}
