package com.learner.language.interfaces.auth

import com.learner.language.application.auth.AuthRefreshFacade
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthRefreshController(
    private val authRefreshFacade: AuthRefreshFacade,
) {

    data class RefreshRequest(
        @field:NotBlank val refreshToken: String,
    )

    @PostMapping("/refresh")
    fun refresh(
        @Valid @RequestBody request: RefreshRequest,
    ): ResponseEntity<AuthDto.ExchangeResponse> {
        val result = authRefreshFacade.refresh(request.refreshToken)
        return ResponseEntity.ok(AuthDto.ExchangeResponse.from(result))
    }
}
