package com.learner.language.service.profile

import com.learner.language.domain.profile.ProfileServiceImpl
import com.learner.language.domain.profile.UpdateMyProfileCommand
import com.learner.language.domain.profile.UserProfile
import com.learner.language.domain.user.UserReader
import com.learner.language.infrastructure.cliplearning.UserSavedClipRepository
import com.learner.language.infrastructure.profile.ProfileSavedClipQueryRepository
import com.learner.language.infrastructure.profile.UserProfileRepository
import com.learner.language.system.security.CustomPasswordEncoder
import com.learner.language.testutils.fixture.UserFixture
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import org.springframework.test.util.ReflectionTestUtils
import java.util.Optional

class ProfileServiceTest : BehaviorSpec({
    val userReader = mockk<UserReader>()
    val userProfileRepository = mockk<UserProfileRepository>()
    val userSavedClipRepository = mockk<UserSavedClipRepository>()
    val profileSavedClipQueryRepository = mockk<ProfileSavedClipQueryRepository>()
    val passwordEncoder = mockk<CustomPasswordEncoder>()

    val profileService = ProfileServiceImpl(
        userReader = userReader,
        userProfileRepository = userProfileRepository,
        userSavedClipRepository = userSavedClipRepository,
        profileSavedClipQueryRepository = profileSavedClipQueryRepository
    )

    afterTest {
        clearMocks(userReader, userProfileRepository, userSavedClipRepository, profileSavedClipQueryRepository)
    }

    given("retrieveProfile is called") {
        val userId = 1L
        val viewerId = 2L
        every { passwordEncoder.encodePassword(any()) } returns "encoded-password"
        val user = UserFixture.createUser(username = "jenny", passwordEncoder = passwordEncoder).also {
            ReflectionTestUtils.setField(it, "id", userId)
        }

        `when`("no user_profile row exists") {
            every { userReader.getUserById(userId) } returns user
            every { userProfileRepository.findByUserId(userId) } returns Optional.empty()
            every { userSavedClipRepository.countByUserId(userId) } returns 3L

            val result = profileService.retrieveProfile(viewerId, userId)

            then("username is used as the fallback display name") {
                result.username shouldBe "jenny"
                result.displayName shouldBe "jenny"
                result.savedClipCount shouldBe 3L
                result.isMe shouldBe false
            }
        }
    }

    given("updateMyProfile is called") {
        val userId = 1L
        every { passwordEncoder.encodePassword(any()) } returns "encoded-password"
        val user = UserFixture.createUser(username = "jenny", passwordEncoder = passwordEncoder).also {
            ReflectionTestUtils.setField(it, "id", userId)
        }
        val command = UpdateMyProfileCommand(
            displayName = "Jenny Kim",
            bio = "english learner",
            profileImageUrl = "https://cdn.example.com/jenny.jpg"
        )

        `when`("user_profile row does not exist yet") {
            every { userReader.getUserById(userId) } returns user
            every { userProfileRepository.findByUserId(userId) } returnsMany listOf(Optional.empty(), Optional.of(UserProfile(user, command.displayName, command.bio, command.profileImageUrl)))
            every { userProfileRepository.save(any()) } answers { firstArg() }
            every { userSavedClipRepository.countByUserId(userId) } returns 0L

            val result = profileService.updateMyProfile(userId, command)

            then("a new profile row is created and returned") {
                result.displayName shouldBe "Jenny Kim"
                result.bio shouldBe "english learner"
                result.profileImageUrl shouldBe "https://cdn.example.com/jenny.jpg"
                result.isMe shouldBe true
            }
        }
    }
})
