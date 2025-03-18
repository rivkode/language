package com.learner.language.domain.user.auth

import com.learner.language.system.security.JwtUtil
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component

@Component
class JwtAuthenticationProvider(
    private val jwtUtil: JwtUtil
) {
    fun authenticate(accessToken: String): Authentication {
        val claims = jwtUtil.getUserId(accessToken)
        val authentication = JwtAuthentication(claims.userId, accessToken)
        val authorities = getAuthorities(claims.authorities)
        return UsernamePasswordAuthenticationToken.authenticated(authentication, accessToken, authorities)
    }

    private fun getAuthorities(authorities: List<String>): List<GrantedAuthority> {
        return authorities.map { SimpleGrantedAuthority(it) }
    }
}
