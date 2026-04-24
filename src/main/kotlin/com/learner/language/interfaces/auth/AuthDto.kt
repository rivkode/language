package com.learner.language.interfaces.auth

import com.learner.language.application.auth.AuthExchangeCommand
import com.learner.language.application.auth.AuthTokenResult
import jakarta.validation.constraints.NotBlank

object AuthDto {
    data class ExchangeRequest(
        @field:NotBlank val code: String,
    ) {
        fun toCommand(): AuthExchangeCommand = AuthExchangeCommand(code)
    }

    data class ExchangeResponse(
        val accessToken: String,
        val refreshToken: String,
        val tokenType: String,
        val expiresIn: Long,
    ) {
        companion object {
            fun from(result: AuthTokenResult): ExchangeResponse = ExchangeResponse(
                accessToken = result.accessToken,
                refreshToken = result.refreshToken,
                tokenType = result.tokenType,
                expiresIn = result.expiresInSeconds,
            )
        }
    }
}
