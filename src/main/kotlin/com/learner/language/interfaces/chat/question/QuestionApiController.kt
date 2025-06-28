package com.learner.language.interfaces.chat.question

import com.learner.language.application.chat.question.QuestionFacade
import com.learner.language.system.login.LoginUser
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/v1/questions")
class QuestionApiController(
    private val questionFacade: QuestionFacade
) {
    @PostMapping
    fun registerQuestion(
        @LoginUser userId: Long,
        @Valid @RequestBody registerRequest: QuestionDto.RegisterRequest
    ): ResponseEntity<QuestionDto.RegisterResponse> {
        val command = registerRequest.toCommand()
        val questionInfo = questionFacade.registerQuestion(command, userId)
        val response = QuestionDto.RegisterResponse(questionInfo)

        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/me")
    fun retrieveMyQuestionList(
        @LoginUser userId: Long
    ): ResponseEntity<QuestionDto.RetrieveListResponse> {
        val questionInfoList = questionFacade.retrieveMyQuestionList(userId)
        val response = QuestionDto.RetrieveListResponse(questionInfoList)

        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

//    @GetMapping("/me/{questionId}")
//    fun retrieveMyQuestion(
//        @LoginUser userId: Long,
//        @PathVariable("questionId") questionId: Long
//    ): ResponseEntity<QuestionDto.RetrieveResponse> {
//        val questionInfo = questionFacade.retrieveMyQuestion(userId, questionId)
//        val response = QuestionDto.RetrieveResponse(questionInfo)
//
//        return ResponseEntity.status(HttpStatus.OK).body(response)
//    }
}
