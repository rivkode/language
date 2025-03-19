package com.learner.language.interfaces.sentence

import com.learner.language.application.sentence.SentenceFacade
import com.learner.language.system.login.LoginUser
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/sentences")
class SentenceApiController(
    private val sentenceFacade: SentenceFacade
) {

    @PostMapping
    fun registerSentence(
        @LoginUser userId: Long,
        @Valid @RequestBody registerRequest: SentenceDto.RegisterRequest
    ): ResponseEntity<SentenceDto.RegisterResponse> {
        val command = registerRequest.toCommand()
        val sentenceInfo = sentenceFacade.registerSentence(command, userId)
        val response = SentenceDto.RegisterResponse(sentenceInfo)

        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/me")
    fun retrieveMySentenceList(
        @LoginUser userId: Long
    ): ResponseEntity<SentenceDto.RetrieveListResponse> {
        val sentenceInfoList = sentenceFacade.retrieveMySentenceList(userId)
        val response = SentenceDto.RetrieveListResponse(sentenceInfoList)

        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @GetMapping("/me/{sentenceId}")
    fun retrieveMySentence(
        @LoginUser userId: Long,
        @PathVariable("sentenceId") sentenceId: Long
    ): ResponseEntity<SentenceDto.RetrieveResponse> {
        val sentenceInfo = sentenceFacade.retrieveMySentence(userId, sentenceId)
        val response = SentenceDto.RetrieveResponse(sentenceInfo)

        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

}
