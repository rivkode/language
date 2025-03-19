package com.learner.language.testutils.persistence

import com.learner.language.domain.user.Role
import com.learner.language.domain.user.User
import com.learner.language.infrastructure.user.UserRepository
import com.learner.language.system.security.CustomPasswordEncoder
import com.learner.language.testutils.fixture.UserFixture

class UserPersistenceUtils(
    private val userRepository: UserRepository,
    private val passwordEncoder: CustomPasswordEncoder
) {
    fun saveNewUser(
        email: String = "email@gmail.com",
        password: String = "password",
        username: String = "username",
        role: Role = Role.USER
    ): User {
        val user = UserFixture.createUser(
            email, password, username, role, passwordEncoder
        )

        return userRepository.save(user)
    }

    fun clearContext() {
        userRepository.deleteAll()
    }
}
