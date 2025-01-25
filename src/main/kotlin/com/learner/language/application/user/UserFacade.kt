package com.learner.language.application.user

import com.learner.language.domain.user.*
import org.springframework.stereotype.Service

@Service
class UserFacade(
    private val userReader: UserReader,
    private val userWriter: UserWriter,
    private val userService: UserService
) {
    fun registerUser(command: UserCommand): UserInfo {
        val userInfo = userService.registerUser(command)

        return userInfo
    }
}
