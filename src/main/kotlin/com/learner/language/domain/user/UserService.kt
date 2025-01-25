package com.learner.language.domain.user

interface UserService {

    fun registerUser(userCommand: UserCommand) : UserInfo

    fun updateUser(userCommand: UserCommand) : UserInfo

}
