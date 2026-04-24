package com.learner.language.interfaces.user

import com.learner.language.domain.user.PublicUserProfileInfo
import com.learner.language.domain.user.UserCommand
import com.learner.language.domain.user.UserInfo
import jakarta.validation.constraints.NotEmpty
import java.time.Instant

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
        val id: Long,
        val userId: Long,
        val username: String,
        val email: String,
        val avatarUrl: String?,
        val provider: String,
        val createdAt: Instant,
    ) {
        constructor(userInfo: UserInfo): this(
            id = userInfo.id,
            userId = userInfo.id,
            username = userInfo.username,
            email = userInfo.email,
            avatarUrl = userInfo.avatarUrl,
            provider = userInfo.provider,
            createdAt = userInfo.createdAt,
        )
    }

    data class PublicProfileResponse(
        val id: Long,
        val userId: Long,
        val username: String,
        val avatarUrl: String?,
        val provider: String,
        val createdAt: Instant,
        val diaryCount: Long,
        val followerCount: Long,
        val followingCount: Long,
        val isFollowing: Boolean,
        val bio: String?,
    ) {
        constructor(info: PublicUserProfileInfo) : this(
            id = info.id,
            userId = info.userId,
            username = info.username,
            avatarUrl = info.avatarUrl,
            provider = info.provider,
            createdAt = info.createdAt,
            diaryCount = info.diaryCount,
            followerCount = info.followerCount,
            followingCount = info.followingCount,
            isFollowing = info.isFollowing,
            bio = info.bio,
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
