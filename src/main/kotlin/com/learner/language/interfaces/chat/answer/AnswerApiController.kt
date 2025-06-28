package com.learner.language.interfaces.chat.answer

import com.learner.language.application.chat.answer.AnswerFacade
import com.learner.language.system.login.LoginUser
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/answers")
class AnswerApiController(
    private val answerFacade: AnswerFacade
) {
    @GetMapping("/{questionId}")
    fun retrieveAnswer(
        @LoginUser userId: Long,
        @PathVariable("questionId") questionId: Long
    ): ResponseEntity<AnswerDto.RetrieveResponse> {
        val answerInfo = answerFacade.retrieveAnswer(userId, questionId)
        val response = AnswerDto.RetrieveResponse(answerInfo)

        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @PostMapping
    fun registerAnswer(
        @LoginUser userId: Long,
        @RequestBody registerAnswer: AnswerDto.RegisterRequest
    ): ResponseEntity<AnswerDto.RegisterResponse> {
        val answerInfo = answerFacade.generateAnswer(userId, registerAnswer)
        val response = AnswerDto.RegisterResponse(answerInfo)

        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }
}
