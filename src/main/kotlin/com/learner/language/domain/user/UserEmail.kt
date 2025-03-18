package com.learner.language.domain.user

import com.learner.language.domain.user.exception.UserBadRequestException
import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class UserEmail(
    email: String
) {
    @Column(name = "email")
    val email: String = email.also {
        validateUserEmail(email)
    }

    private fun validateUserEmail(email: String) {
        if (email.isEmpty()) {
            throw UserBadRequestException("email은 필수 값 입니다")
        }
    }

}
