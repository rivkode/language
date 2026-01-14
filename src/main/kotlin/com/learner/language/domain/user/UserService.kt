package com.learner.language.domain.user

import com.learner.language.interfaces.user.UserDto

interface UserService {

    fun saveUser(userCommand: UserCommand) : UserInfo
    fun updateUser(userCommand: UserCommand) : UserInfo
    fun checkEmailDuplicate(email: String)
    fun validateNumber(request: UserDto.ValidateNumberRequest)
    fun getUser(userId: Long?): UserInfo
}
