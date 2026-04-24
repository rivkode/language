package com.learner.language.application.auth

import com.learner.language.domain.auth.AuthTokenSnapshot

interface AuthCodeStore {
    fun issue(snapshot: AuthTokenSnapshot): String
    fun consume(code: String): AuthTokenSnapshot?
}
