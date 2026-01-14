package com.learner.language.application.user

import com.learner.language.common.RandomNumber
import com.learner.language.domain.email.MailService
import com.learner.language.domain.user.*
import com.learner.language.interfaces.user.UserDto
import org.springframework.stereotype.Service

@Service
class UserFacade(
    private val userReader: UserReader,
    private val userWriter: UserWriter,
    private val userService: UserService,
    private val mailService: MailService,
    private val randomNumber: RandomNumber
) {

    fun sendValidationNumberToEmail(email: String) {
        userService.checkEmailDuplicate(email)
        val authNumber = randomNumber.generateRandomNumber()
        mailService.sendValidateEmail(email, authNumber)
    }

    fun validateNumber(request: UserDto.ValidateNumberRequest) {
        userService.validateNumber(request)
    }



    fun registerUser(command: UserCommand): UserInfo {
        val userInfo = userService.saveUser(command)

        return userInfo
    }

    fun retrieveUserInfo(userId: Long): UserInfo {
        val userInfo = userService.getUser(userId)

        return userInfo
    }
}
