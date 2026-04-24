package com.learner.language.interfaces.auth

import com.learner.language.application.auth.AuthLogoutFacade
import com.learner.language.system.login.LoginUser
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthLogoutController(
    private val authLogoutFacade: AuthLogoutFacade,
) {
    @PostMapping("/logout")
    fun logout(@LoginUser userId: Long): ResponseEntity<Void> {
        authLogoutFacade.logout(userId)
        return ResponseEntity.noContent().build()
    }
}
