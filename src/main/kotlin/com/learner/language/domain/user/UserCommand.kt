package com.learner.language.domain.user

data class UserCommand(
    val username: String,
    val email: String,
    val password: String
) {
    fun toEntity(): User {
        return User(
            username = username,
            email = email,
            password = password,
            role = Role.USER
        )
    }

}
