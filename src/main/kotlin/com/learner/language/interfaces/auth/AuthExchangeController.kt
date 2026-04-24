package com.learner.language.interfaces.auth

import com.learner.language.application.auth.AuthExchangeFacade
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthExchangeController(
    private val authExchangeFacade: AuthExchangeFacade,
) {
    @PostMapping("/exchange")
    fun exchange(
        @Valid @RequestBody request: AuthDto.ExchangeRequest,
    ): ResponseEntity<AuthDto.ExchangeResponse> {
        val result = authExchangeFacade.exchange(request.toCommand())
        return ResponseEntity.ok(AuthDto.ExchangeResponse.from(result))
    }
}
