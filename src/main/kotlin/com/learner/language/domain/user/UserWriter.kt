package com.learner.language.domain.user

interface UserWriter {
    fun save(user: User) : User

    fun update(user: User) : User

}
