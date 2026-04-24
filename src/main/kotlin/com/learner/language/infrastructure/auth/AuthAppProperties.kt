package com.learner.language.infrastructure.auth

import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

@Validated
@ConfigurationProperties(prefix = "app")
data class AuthAppProperties(
    @field:NotBlank val frontendBaseUrl: String,
    @field:NotBlank val backendBaseUrl: String,
)
