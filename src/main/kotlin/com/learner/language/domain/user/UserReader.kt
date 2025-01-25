package com.learner.language.domain.user

interface UserReader {
    fun getUserById(userId: Long): User
    fun getUserByEmail(email: String): User
}
