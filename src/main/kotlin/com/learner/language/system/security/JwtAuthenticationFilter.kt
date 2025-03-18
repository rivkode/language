package com.learner.language.system.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.learner.language.domain.user.UserRefreshToken
import com.learner.language.infrastructure.user.UserRefreshTokenRepository
import com.learner.language.interfaces.user.UserDto
import com.learner.language.utils.CookieUtil
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.io.IOException

class JwtAuthenticationFilter(
    private val jwtUtil: JwtUtil,
    private val userRefreshTokenRepository: UserRefreshTokenRepository,
    private val cookieUtil: CookieUtil
) : UsernamePasswordAuthenticationFilter() {
    init {
        setFilterProcessesUrl("/api/v1/users/login")
    }

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        return try {
            val loginRequest: UserDto.LoginRequest = ObjectMapper().readValue(request.inputStream, UserDto.LoginRequest::class.java)
            val authorities = getAuthorities(listOf("ROLE_USER"))
            log.info(">> Login Request login email: " + loginRequest.email)

            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    loginRequest.email,
                    loginRequest.password,
                    authorities
                )
            )
        } catch (e: IOException) {
            throw RuntimeException(e.message)
        }
    }

    private fun getAuthorities(authorities: List<String>): List<GrantedAuthority> {
        return authorities.map { SimpleGrantedAuthority(it) }
    }

    override fun successfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
        authentication: Authentication
    ) {
        val userDetails = authentication.principal as UserDetailsImpl

        // JWT Access Token 생성
        val token = jwtUtil.createToken(userDetails.getUserId(), userDetails.getEmail())

        log.info(">> Login Succeed")

        handleLoginSuccess(response, userDetails, token)
    }

    override fun unsuccessfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        failed: AuthenticationException
    ) {
        response.status = HttpServletResponse.SC_BAD_REQUEST
        response.contentType = "application/json"

        val result = ObjectMapper().writeValueAsString(
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("login failure")
        )

        response.outputStream.print(result)
    }

    private fun handleLoginSuccess(response: HttpServletResponse, userDetails: UserDetailsImpl, token: String) {
        val userRefreshToken = getOrGenerate(userDetails.getUserId())

        cookieUtil.addRefreshTokenCookie(response, userRefreshToken)

        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token)

        response.status = HttpServletResponse.SC_OK
        response.contentType = "application/json"
    }

    private fun getOrGenerate(userId: Long): UserRefreshToken {
        val userRefreshToken = userRefreshTokenRepository.findByUserId(userId)

        return if (userRefreshToken.isPresent) {
            userRefreshToken.get().apply {
                log.info("Before userRefreshToken: ${refreshToken.refreshToken}")
                updateRefreshToken()
                log.info("After userRefreshToken: ${refreshToken.refreshToken}")
                userRefreshTokenRepository.save(this)
            }
        } else {
            UserRefreshToken(userId).also {
                log.info("generate RefreshToken value: ${it.refreshToken.refreshToken}")
                userRefreshTokenRepository.save(it)
            }
        }
    }
}
