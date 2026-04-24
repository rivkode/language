package com.learner.language.infrastructure.auth

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = "app.auth.auth-code")
data class AuthCodeProperties(
    val ttl: Duration = Duration.ofSeconds(60),
)
