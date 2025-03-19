package com.learner.language.testutils.fixture

import com.learner.language.domain.user.Role
import com.learner.language.domain.user.User
import com.learner.language.domain.user.UserEmail
import com.learner.language.domain.user.UserPassword
import com.learner.language.system.security.CustomPasswordEncoder

object UserFixture {
    fun createUser(
        email: String = "email@gmail.com",
        password: String = "password",
        username: String = "username",
        role: Role = Role.USER,
        passwordEncoder: CustomPasswordEncoder
    ): User {
        return User(
            email = UserEmail(email),
            password = UserPassword(password, passwordEncoder),
            username = username,
            role = role
        )
    }
}
