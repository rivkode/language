package com.learner.language.system.security

import com.learner.language.domain.user.AuthProvider
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component

@Component
class OAuthStateCookieManager {

    fun set(response: HttpServletResponse, provider: AuthProvider, value: String) {
        val cookie = Cookie(cookieName(provider), value).apply {
            isHttpOnly = true
            secure = true
            path = "/"
            maxAge = MAX_AGE_SECONDS
            setAttribute("SameSite", "Lax")
        }
        response.addCookie(cookie)
    }

    fun get(request: HttpServletRequest, provider: AuthProvider): String? {
        val name = cookieName(provider)
        return request.cookies?.firstOrNull { it.name == name }?.value
    }

    fun clear(response: HttpServletResponse, provider: AuthProvider) {
        val cookie = Cookie(cookieName(provider), "").apply {
            isHttpOnly = true
            secure = true
            path = "/"
            maxAge = 0
            setAttribute("SameSite", "Lax")
        }
        response.addCookie(cookie)
    }

    private fun cookieName(provider: AuthProvider): String =
        "$COOKIE_PREFIX${provider.name}"

    companion object {
        private const val COOKIE_PREFIX = "LANGUAGE_OAUTH_STATE_"
        private const val MAX_AGE_SECONDS = 5 * 60
    }
}
