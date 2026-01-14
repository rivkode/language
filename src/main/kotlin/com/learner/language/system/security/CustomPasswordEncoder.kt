package com.learner.language.system.security

interface CustomPasswordEncoder {
    fun encodePassword(rawPassword: String): String
    fun matchesPassword(rawPassword: String, encodedPassword: String): Boolean
    fun nonMatchesPassword(rawPassword: String, encodedPassword: String): Boolean
}