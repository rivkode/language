package com.learner.language.interfaces.validation

import com.learner.language.application.validation.DiaryValidationFacade
import com.learner.language.system.login.LoginUser
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/diaries/validate")
class DiaryValidationController(
    private val validationFacade: DiaryValidationFacade,
) {

    @PostMapping
    fun validate(
        @LoginUser userId: Long,
        @Valid @RequestBody request: DiaryValidationDto.ValidateRequest,
    ): ResponseEntity<DiaryValidationDto.ValidationResponse> {
        val result = validationFacade.validate(request.lines)
        return ResponseEntity.ok(DiaryValidationDto.ValidationResponse.from(result))
    }

    @PostMapping("/line")
    fun validateLine(
        @LoginUser userId: Long,
        @Valid @RequestBody request: DiaryValidationDto.ValidateLineRequest,
    ): ResponseEntity<DiaryValidationDto.LineResponse> {
        val result = validationFacade.validateLine(request.lineIndex, request.text)
        return ResponseEntity.ok(DiaryValidationDto.LineResponse.from(result))
    }
}
