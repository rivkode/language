package com.learner.language.domain.user

interface UserWriter {
    fun registerUser(user: User) : User

    fun updateUser(user: User) : User

}
