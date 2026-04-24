package com.learner.language.infrastructure.auth

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

@Validated
@ConfigurationProperties(prefix = "app.auth.oauth")
data class OAuthProperties(
    @field:Valid val kakao: Provider,
    @field:Valid val naver: Provider,
    @field:Valid val google: Provider,
) {
    data class Provider(
        @field:NotBlank val clientId: String,
        @field:NotBlank val clientSecret: String,
        @field:NotBlank val redirectUri: String,
        @field:NotBlank val authorizeUri: String,
        @field:NotBlank val tokenUri: String,
        @field:NotBlank val userInfoUri: String,
        @field:NotBlank val scope: String,
    )
}
