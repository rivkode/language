package com.learner.language.system.security

import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder

class BCryptCustomPasswordEncoder : CustomPasswordEncoder {
    private val passwordEncoder: PasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()
    override fun encodePassword(rawPassword: String): String {
        return passwordEncoder.encode(rawPassword)
    }

    override fun matchesPassword(rawPassword: String, encodedPassword: String): Boolean {
        return passwordEncoder.matches(rawPassword, encodedPassword)
    }

    override fun nonMatchesPassword(rawPassword: String, encodedPassword: String): Boolean {
        return !matchesPassword(rawPassword, encodedPassword)
    }
}