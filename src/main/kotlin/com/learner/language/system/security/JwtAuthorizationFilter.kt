package com.learner.language.system.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.learner.language.domain.user.auth.JwtAuthenticationProvider
import com.learner.language.infrastructure.user.UserRefreshTokenRepository
import com.learner.language.utils.CookieUtil
import io.jsonwebtoken.Claims
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

class JwtAuthorizationFilter(
    private val jwtUtil: JwtUtil,
    private val userDetailsService: UserDetailsServiceImpl,
    private val authenticationProvider: JwtAuthenticationProvider,
    private val userRefreshTokenRepository: UserRefreshTokenRepository,
    private val cookieUtil: CookieUtil
) : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val tokenValue = jwtUtil.getTokenFromRequest(request)

        if (tokenValue != null) {
            if (tokenValue.isNotBlank()) {
                if (isNotValidate(request, response, tokenValue)) return

                val info = getClaims(response, tokenValue) ?: return

                if (userInfoInAuthentication(tokenValue)) return
            }
        }

        filterChain.doFilter(request, response)
    }


    private fun isNotValidate(request: HttpServletRequest, response: HttpServletResponse, tokenValue: String): Boolean {
        val tokenStatus = jwtUtil.validateToken(tokenValue)

        if (tokenStatus == JwtUtil.TokenStatus.INVALID) {
            return true
        } else if (tokenStatus == JwtUtil.TokenStatus.EXPIRED) {
            log.info("get refreshtokenfromrequest")
            val refreshToken = getRefreshTokenFromRequest(request)

            if (refreshToken == "expiration") {
                log.info("재로그인 요청합니다")
                response.status = HttpServletResponse.SC_BAD_REQUEST
                response.contentType = "application/json"
                return true
            }

            log.info("check read refreshToken")
            log.info("refreshToken: $refreshToken")

            val userRefreshTokenEntity = userRefreshTokenRepository.findByRefreshTokenRefreshToken(refreshToken)
            val userId = userRefreshTokenEntity.get().userId
            val email = userDetailsService.getUserEmail(userId)
            log.info("check after read refreshToken")

            val newAccessToken = jwtUtil.createToken(userId, email)

            userRefreshTokenEntity.get().updateRefreshToken()
            userRefreshTokenRepository.save(userRefreshTokenEntity.get())
            cookieUtil.addRefreshTokenCookie(response, userRefreshTokenEntity.get())

            response.addHeader(JwtUtil.AUTHORIZATION_HEADER, newAccessToken)

            response.status = HttpServletResponse.SC_OK
            response.contentType = "application/json"
            return true
        }

        return false
    }

    private fun getRefreshTokenFromRequest(request: HttpServletRequest): String {
        val cookies = request.cookies
        if (cookies != null) {
            for (cookie in cookies) {
                if (cookie.name == "refreshToken") {
                    return cookie.value
                }
            }
        }
        return "expiration"
    }

    private fun getClaims(response: HttpServletResponse, tokenValue: String): Claims? {
        return try {
            jwtUtil.getUserInfoFromToken(tokenValue)
        } catch (e: Exception) {
            response.status = HttpServletResponse.SC_BAD_REQUEST
            response.contentType = "application/json"
            val result = ObjectMapper().writeValueAsString(
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body("INVALID_TOKEN")
            )
            response.outputStream.print(result)
            null
        }
    }

    private fun userInfoInAuthentication(tokenValue: String): Boolean {
        return try {
            setAuthentication(tokenValue)
            false
        } catch (e: Exception) {
            log.error(e.message)
            true
        }
    }

    fun setAuthentication(tokenValue: String) {
        val authentication = authenticationProvider.authenticate(tokenValue)
        SecurityContextHolder.getContext().authentication = authentication
    }

    private fun createAuthentication(account: String): Authentication {
        val userDetails = userDetailsService.loadUserByUsername(account) as UserDetailsImpl
        val userId = userDetails.getUserId()
        return UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
    }
}
