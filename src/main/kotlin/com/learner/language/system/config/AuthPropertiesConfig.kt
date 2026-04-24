package com.learner.language.system.config

import com.learner.language.infrastructure.auth.AuthAppProperties
import com.learner.language.infrastructure.auth.AuthCodeProperties
import com.learner.language.infrastructure.auth.OAuthProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@EnableScheduling
@EnableConfigurationProperties(
    OAuthProperties::class,
    AuthCodeProperties::class,
    AuthAppProperties::class,
    CorsProperties::class,
)
class AuthPropertiesConfig
