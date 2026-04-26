package com.learner.language.interfaces.cliplearning

import com.learner.language.application.cliplearning.ClipLearningService
import com.learner.language.system.login.LoginUser
import jakarta.validation.Valid
import jakarta.validation.constraints.Positive
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.validation.annotation.Validated

@RestController
@Validated
@RequestMapping("/api/v1/clip-learning")
class ClipLearningApiController(
    private val clipLearningService: ClipLearningService
) {
    @GetMapping("/feed")
    fun retrieveFeed(
        @LoginUser userId: Long,
        @Valid @ModelAttribute request: ClipLearningFeedDto.FeedRequest
    ): ResponseEntity<ClipLearningFeedDto.FeedResponse> {
        val response = clipLearningService.retrieveFeed(userId, request)

        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @GetMapping("/clips/{clipId}")
    fun retrieveClip(
        @LoginUser userId: Long,
        @Positive(message = "clipId must be positive")
        @PathVariable("clipId") clipId: Long
    ): ResponseEntity<ClipLearningClipPageItem> {
        val response = clipLearningService.retrieveClip(userId, clipId)

        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @GetMapping("/transcript")
    fun retrieveTranscript(
        @Valid @ModelAttribute request: ClipLearningTranscriptDto.TranscriptRequest
    ): ResponseEntity<ClipLearningTranscriptDto.TranscriptResponse> {
        val response = clipLearningService.retrieveTranscript(request)

        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @PostMapping("/clips/{clipId}/save")
    fun saveClip(
        @LoginUser userId: Long,
        @Positive(message = "clipId must be positive")
        @PathVariable("clipId") clipId: Long,
        @Valid @RequestBody request: ClipLearningSaveDto.SaveRequest
    ): ResponseEntity<ClipLearningSaveDto.SaveResponse> {
        val response = clipLearningService.saveClip(userId, clipId, request)

        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @PostMapping("/progress")
    fun updateProgress(
        @LoginUser userId: Long,
        @Valid @RequestBody request: ClipLearningProgressDto.ProgressRequest
    ): ResponseEntity<ClipLearningProgressDto.ProgressResponse> {
        val response = clipLearningService.updateProgress(userId, request)

        return ResponseEntity.status(HttpStatus.OK).body(response)
    }
}
