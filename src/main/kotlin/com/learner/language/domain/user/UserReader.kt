package com.learner.language.domain.user

interface UserReader {
    fun getUserById(userId: Long): User
    fun getUserByEmail(email: String): User
    fun findByEmail(email: String): User?
    fun findByProvider(provider: AuthProvider, externalId: String): User?
    fun existsByEmail(userEmail: UserEmail): Boolean
}
