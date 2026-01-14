package com.learner.language.interfaces.user

import com.learner.language.domain.user.UserCommand
import com.learner.language.domain.user.UserInfo
import jakarta.validation.constraints.NotEmpty

class UserDto {
    data class RegisterRequest(
        @NotEmpty(message = "email은 필수 입력값입니다.")
        val email: String,
        @NotEmpty(message = "username은 필수 입력값입니다.")
        val username: String,
        @NotEmpty(message = "password은 필수 입력값입니다.")
        val password: String
    ) {
        fun toCommand(): UserCommand {
            return UserCommand(
                email = email,
                username = username,
                password = password
            )
        }
    }

    data class LoginRequest(
        @NotEmpty(message = "email은 필수 입력값입니다.")
        val email: String = "",
        @NotEmpty(message = "password는 필수 입력값입니다.")
        val password: String = ""
    ) {
    }

    data class LoginResponse(
        @NotEmpty(message = "login")
        val response: String
    )

    data class RegisterResponse(
        val username: String
    ) {
        constructor(userInfo: UserInfo) : this(
            username = userInfo.username
        )
    }

    data class InfoResponse(
        val username: String,
        val email: String
    ) {
        constructor(userInfo: UserInfo): this(
            username = userInfo.username,
            email = userInfo.email
        )
    }

    data class ValidateNumberRequest(
        @NotEmpty(message = "email 은 필수 입력값입니다.")
        val email: String,
        val validationNumber: String?
    )

    data class ValidateNumberResponse(
        val response: String
    )
}
