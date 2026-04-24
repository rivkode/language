package com.learner.language.system.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.cors")
data class CorsProperties(
    val allowedOrigins: List<String> = listOf("http://localhost:3000"),
    val allowedMethods: List<String> = listOf("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"),
    val allowedHeaders: List<String> = listOf("Authorization", "Content-Type"),
    val allowCredentials: Boolean = false,
    val exposedHeaders: List<String> = listOf("Authorization"),
    val maxAge: Long = 3600L,
)
