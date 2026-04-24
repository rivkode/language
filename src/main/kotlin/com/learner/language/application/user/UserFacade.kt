package com.learner.language.application.user

import com.learner.language.common.RandomNumber
import com.learner.language.domain.email.MailService
import com.learner.language.domain.user.PublicUserProfileInfo
import com.learner.language.domain.user.User
import com.learner.language.domain.user.UserCommand
import com.learner.language.domain.user.UserInfo
import com.learner.language.domain.user.UserReader
import com.learner.language.domain.user.UserService
import com.learner.language.domain.user.UserWriter
import com.learner.language.infrastructure.diary.DiaryRepository
import com.learner.language.infrastructure.profile.UserProfileRepository
import com.learner.language.interfaces.user.UserDto
import org.springframework.stereotype.Service
import java.time.ZoneOffset

@Service
class UserFacade(
    private val userReader: UserReader,
    private val userWriter: UserWriter,
    private val userService: UserService,
    private val mailService: MailService,
    private val randomNumber: RandomNumber,
    private val userProfileRepository: UserProfileRepository,
    private val diaryRepository: DiaryRepository,
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
        return userService.saveUser(command)
    }

    fun retrieveUserInfo(userId: Long): UserInfo {
        val user = userReader.getUserById(userId)
        val avatarUrl = userProfileRepository.findByUserId(user.id)
            .map { it.profileImageUrl }
            .orElse(null)
        return UserInfo.of(user, avatarUrl = avatarUrl)
    }

    fun retrievePublicProfile(viewerUserId: Long, targetUserId: Long): PublicUserProfileInfo {
        val user = userReader.getUserById(targetUserId)
        val profile = userProfileRepository.findByUserId(user.id).orElse(null)
        val diaryCount = diaryRepository.countByUserIdAndIsPublic(user.id, true)
        return toPublicInfo(user, profile?.profileImageUrl, profile?.bio, diaryCount)
    }

    private fun toPublicInfo(
        user: User,
        avatarUrl: String?,
        bio: String?,
        diaryCount: Long,
    ): PublicUserProfileInfo = PublicUserProfileInfo(
        id = user.id,
        userId = user.id,
        username = user.username,
        avatarUrl = avatarUrl,
        provider = user.provider.name.lowercase(),
        createdAt = user.createdAt.toInstant(ZoneOffset.UTC),
        diaryCount = diaryCount,
        followerCount = 0,
        followingCount = 0,
        isFollowing = false,
        bio = bio,
    )
}
