package com.learner.language.utils

import com.learner.language.domain.user.UserRefreshToken
import com.learner.language.system.security.JwtUtil
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.util.SerializationUtils
import java.util.*

@Component
object CookieUtil {
    private val log = LoggerFactory.getLogger(CookieUtil::class.java)

    fun getCookie(request: HttpServletRequest, name: String): Optional<Cookie> {
        val cookies = request.cookies ?: return Optional.empty()

        return cookies.find { it.name == name }
            ?.also { log.info("refresh Token: ${it.name}") }
            ?.let { Optional.of(it) } ?: Optional.empty()
    }

    fun addCookie(response: HttpServletResponse, name: String, value: String, maxAge: Int) {
        val cookie = Cookie(name, value).apply {
            path = "/"
            isHttpOnly = true
            this.maxAge = maxAge
        }
        response.addCookie(cookie)
    }

    fun deleteCookie(request: HttpServletRequest, response: HttpServletResponse, name: String) {
        request.cookies?.filter { it.name == name }?.forEach { cookie ->
            cookie.apply {
                value = ""
                path = "/"
                maxAge = 0
            }
            response.addCookie(cookie)
        }
    }

    fun serialize(obj: Any): String {
        return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(obj))
    }

    fun <T> deserialize(cookie: Cookie, cls: Class<T>): T {
        return cls.cast(
            SerializationUtils.deserialize(
                Base64.getUrlDecoder().decode(cookie.value)
            )
        )
    }

    fun addRefreshTokenCookie(response: HttpServletResponse, userRefreshToken: UserRefreshToken) {
        val refreshToken = userRefreshToken.refreshToken.refreshToken
        val cookie = Cookie("refreshToken", refreshToken).apply {
            isHttpOnly = true
            secure = true // HTTPS 사용 시 true
            path = "/"
            maxAge = 5 * 60 // 5분
            setAttribute("SameSite", "Strict")
        }
        response.addCookie(cookie)
    }

    fun addAccessTokenRefreshTokenCookie(response: HttpServletResponse, userRefreshToken: UserRefreshToken, accessToken: String) {
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, accessToken)

        val refreshToken = userRefreshToken.refreshToken.refreshToken
        val cookie = Cookie("refreshToken", refreshToken).apply {
            isHttpOnly = true
            secure = true
            path = "/"
            maxAge = 5 * 60 // 5 min
            setAttribute("SameSite", "Strict")
        }
        response.addCookie(cookie)
    }
}
