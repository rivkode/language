package com.learner.language.interfaces.sentence

import com.learner.language.application.sentence.SentenceFacade
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
        @Valid @RequestBody registerRequest: SentenceDto.RegisterRequest
    ): ResponseEntity<SentenceDto.RegisterResponse> {
        val command = registerRequest.toCommand()
        val sentenceInfo = sentenceFacade.registerSentence(command)
        val response = SentenceDto.RegisterResponse(sentenceInfo)

        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

}