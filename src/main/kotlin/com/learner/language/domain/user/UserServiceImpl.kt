package com.learner.language.domain.user

import com.learner.language.domain.email.MailService
import com.learner.language.domain.user.auth.AuthAuthenticationException
import com.learner.language.domain.user.exception.UserBadRequestException
import com.learner.language.interfaces.user.UserDto
import com.learner.language.system.security.CustomPasswordEncoder
import com.learner.language.utils.RedisUtil
import org.springframework.stereotype.Component

@Component
class UserServiceImpl(
    private val userReader: UserReader,
    private val userWriter: UserWriter,
    private val passwordEncoder: CustomPasswordEncoder,
    private val mailService: MailService,
    private val redisUtil: RedisUtil
) : UserService {
    override fun saveUser(userCommand: UserCommand): UserInfo {
        val initUser = userCommand.toEntity(passwordEncoder)
        val user = userWriter.save(initUser)

        return UserInfo(user)
    }

    override fun updateUser(userCommand: UserCommand): UserInfo {
        val updateUser = userCommand.toEntity(passwordEncoder)
        val user = userWriter.update(updateUser)

        return UserInfo(user)
    }

    override fun checkEmailDuplicate(email: String) {
        val userEmail = UserEmail(email)
        val isEmailDuplicated = userReader.existsByEmail(userEmail)

        if (isEmailDuplicated) {
            throw UserBadRequestException("check Email Duplicate >> 유저 email: " + email + "은 이미 존재합니다")
        }
    }

    override fun validateNumber(request: UserDto.ValidateNumberRequest) {
        val isAuthorize = mailService.checkAuthNumber(request.email, request.validationNumber ?: "")

        if (!isAuthorize) {
            redisUtil.setDataExpire(request.email + request.validationNumber, "false", 60*5L)
            throw UserBadRequestException("validateNumber >> 유저 email: " + request.email + "이메일 인증시 인증번호가 일치하지 않습니다")
        }

        redisUtil.setDataExpire(request.email + request.validationNumber, "true", 60*5L)
    }

    override fun getUser(userId: Long?): UserInfo {
        if (userId == null) {
            throw AuthAuthenticationException("null")
        }
        val user = userReader.getUserById(userId)

        return UserInfo(user)
    }

}
