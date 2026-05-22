package com.learner.language.interfaces.shorts

import com.learner.language.application.shorts.ShortsService
import com.learner.language.system.login.LoginUser
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Validated
@RequestMapping("/api/v1/shorts")
class ShortsApiController(
    private val shortsService: ShortsService,
) {
    @GetMapping("/feed")
    fun retrieveFeed(
        @LoginUser userId: Long,
        @Valid @ModelAttribute request: ShortsFeedDto.FeedRequest,
    ): ResponseEntity<ShortsFeedDto.FeedResponse> {
        val response = shortsService.retrieveFeed(userId, request)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }
}
