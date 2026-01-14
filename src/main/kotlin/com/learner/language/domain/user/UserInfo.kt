package com.learner.language.domain.user

data class UserInfo(
    val id: Long,
    val username: String,
    val email: String
) {
    constructor(user: User) : this(
        id = user.id,
        username = user.username,
        email = user.email.email
    )
}
