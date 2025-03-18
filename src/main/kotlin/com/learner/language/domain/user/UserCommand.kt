package com.learner.language.domain.user

import com.learner.language.system.security.CustomPasswordEncoder

data class UserCommand(
    val username: String,
    val email: String,
    val password: String
) {
    fun toEntity(passwordEncoder: CustomPasswordEncoder): User {
        return User(
            username = username,
            email = UserEmail(email),
            password = UserPassword(password, passwordEncoder),
            role = Role.USER
        )
    }

}
