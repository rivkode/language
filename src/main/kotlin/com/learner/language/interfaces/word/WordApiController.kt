package com.learner.language.interfaces.word

import com.learner.language.application.word.WordFacade
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/words")
class WordApiController(
    private val wordFacade: WordFacade
) {

    @GetMapping("/me")
    fun retrieveMyWord(
        @Valid @RequestBody retrieveRequest: WordDto.RetrieveMyWordRequest
    ): ResponseEntity<WordDto.RetrieveMyWordResponse> {
        val command = retrieveRequest.toCommand()
        val wordInfo = wordFacade.retrieveMyWord(command)
        val response = WordDto.RetrieveMyWordResponse(wordInfo)

        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

}
