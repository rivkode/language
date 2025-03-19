package com.learner.language.interfaces.word

import com.learner.language.application.word.WordFacade
import com.learner.language.domain.word.Part
import com.learner.language.system.login.LoginUser
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/words")
class WordApiController(
    private val wordFacade: WordFacade
) {

    @GetMapping("/me")
    fun retrieveMyWord(
        @LoginUser userId: Long
    ): ResponseEntity<WordDto.RetrieveWordInfoListResponse> {
        val wordInfoList = wordFacade.retrieveMyWord(userId)
        val response = WordDto.RetrieveWordInfoListResponse(wordInfoList)

        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @GetMapping("/choice")
    fun retrieveChoiceWord(
        @LoginUser userId: Long,
        @RequestParam("part") part: Part,
        @RequestParam("lastWordId") lastWordId: Long?,
    ): ResponseEntity<WordDto.RetrieveWordInfoListResponse> {
        val choiceWords = wordFacade.retrieveChoice(part=part, userId=userId, lastWordId=lastWordId)
        val response = WordDto.RetrieveWordInfoListResponse(wordInfoList=choiceWords)

        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @PostMapping("/choice")
    fun registerChoiceWord(
        @LoginUser userId: Long,
        @Valid @RequestBody registerRequest: WordDto.RegisterChoiceRequest
    ) : ResponseEntity<WordDto.RetrieveWordResponse> {
        val command = registerRequest.toCommand(userId=userId)
        val selectionWord = wordFacade.registerChoiceWord(command=command)
        val response = WordDto.RetrieveWordResponse(wordInfo=selectionWord)

        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

}
