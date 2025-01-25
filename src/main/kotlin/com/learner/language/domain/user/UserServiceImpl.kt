package com.learner.language.domain.user

import org.springframework.stereotype.Component

@Component
class UserServiceImpl(
    private val userReader: UserReader,
    private val userWriter: UserWriter
) : UserService {
    override fun registerUser(userCommand: UserCommand): UserInfo {
        val initUser = userCommand.toEntity()
        val user = userWriter.registerUser(initUser)

        return UserInfo(user)
    }

    override fun updateUser(userCommand: UserCommand): UserInfo {
        val updateUser = userCommand.toEntity()
        val user = userWriter.updateUser(updateUser)

        return UserInfo(user)
    }

}
