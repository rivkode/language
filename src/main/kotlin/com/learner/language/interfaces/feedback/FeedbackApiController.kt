package com.learner.language.interfaces.feedback

import com.learner.language.application.feedback.FeedbackFacade
import com.learner.language.system.login.LoginUser
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/feedbacks")
class FeedbackApiController(
    private val feedbackFacade: FeedbackFacade
) {
    @GetMapping("/{sentenceId}")
    fun retrieveFeedback(
        @LoginUser userId: Long,
        @PathVariable("sentenceId") sentenceId: Long
    ): ResponseEntity<FeedbackDto.RetrieveResponse> {
        val feedbackInfo = feedbackFacade.retrieveFeedback(userId, sentenceId)
        val response = FeedbackDto.RetrieveResponse(feedbackInfo)

        return ResponseEntity.status(HttpStatus.OK).body(response)
    }
}
