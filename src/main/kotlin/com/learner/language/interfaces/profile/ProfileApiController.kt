package com.learner.language.interfaces.profile

import com.learner.language.application.profile.ProfileFacade
import com.learner.language.system.login.LoginUser
import jakarta.validation.Valid
import jakarta.validation.constraints.Positive
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/profiles")
class ProfileApiController(
    private val profileFacade: ProfileFacade
) {
    @GetMapping("/me")
    fun retrieveMyProfile(
        @LoginUser userId: Long
    ): ResponseEntity<ProfileDto.ProfileResponse> {
        val response = ProfileDto.ProfileResponse(profileFacade.retrieveMyProfile(userId))

        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @GetMapping("/{userId}")
    fun retrieveProfile(
        @LoginUser loginUserId: Long,
        @Positive(message = "userId must be positive")
        @PathVariable("userId") userId: Long
    ): ResponseEntity<ProfileDto.ProfileResponse> {
        val response = ProfileDto.ProfileResponse(profileFacade.retrieveProfile(loginUserId, userId))

        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @GetMapping("/{userId}/saved-clips")
    fun retrieveSavedClips(
        @Positive(message = "userId must be positive")
        @PathVariable("userId") userId: Long,
        @Valid @ModelAttribute request: ProfileSavedClipDto.FeedRequest
    ): ResponseEntity<ProfileSavedClipDto.FeedResponse> {
        val response = ProfileSavedClipDto.FeedResponse(
            profileFacade.retrieveSavedClips(
                profileUserId = userId,
                cursor = request.cursor,
                size = request.size,
                category = request.category
            )
        )

        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @PatchMapping("/me")
    fun updateMyProfile(
        @LoginUser userId: Long,
        @Valid @RequestBody request: ProfileDto.UpdateMyProfileRequest
    ): ResponseEntity<ProfileDto.ProfileResponse> {
        val response = ProfileDto.ProfileResponse(profileFacade.updateMyProfile(userId, request.toCommand()))

        return ResponseEntity.status(HttpStatus.OK).body(response)
    }
}
