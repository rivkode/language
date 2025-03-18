package com.learner.language.system.security

import com.learner.language.domain.user.auth.CustomClaims
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import java.security.Key
import java.time.Duration
import java.util.*

@Component
class JwtUtil {
    companion object {
        const val AUTHORIZATION_HEADER = "Authorization"
        const val BEARER_PREFIX = "Bearer "
        private val TOKEN_TIME = Duration.ofDays(5).toMillis()
        private val signatureAlgorithm = SignatureAlgorithm.HS256
        val logger: Logger = LoggerFactory.getLogger("JWT 관련 로그")
    }

    @Value("\${jwt.secret}")
    private lateinit var secretKey: String

    private lateinit var key: Key

    @PostConstruct
    fun init() {
        val bytes = Base64.getDecoder().decode(secretKey)
        key = Keys.hmacShaKeyFor(bytes)
    }

    fun createToken(userId: Long, email: String): String {
        val date = Date()
        return BEARER_PREFIX +
            Jwts.builder()
            .setSubject(email)
            .claim("id", userId)
            .setExpiration(Date(date.time + TOKEN_TIME))
            .setIssuedAt(date)
            .signWith(key, signatureAlgorithm)
            .compact()
    }

    fun getTokenFromRequest(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader(AUTHORIZATION_HEADER)
        return if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            bearerToken.substring(BEARER_PREFIX.length)
        } else null
    }

    fun validateToken(token: String): TokenStatus {
        return try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
            TokenStatus.VALID
        } catch (e: SecurityException) {
            logger.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.")
            TokenStatus.INVALID
        } catch (e: MalformedJwtException) {
            logger.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.")
            TokenStatus.INVALID
        } catch (e: ExpiredJwtException) {
            logger.error("Expired JWT token, 만료된 JWT token 입니다.")
            logger.info("after expired error log")
            TokenStatus.EXPIRED
        } catch (e: UnsupportedJwtException) {
            logger.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.")
            TokenStatus.INVALID
        } catch (e: IllegalArgumentException) {
            logger.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.")
            TokenStatus.INVALID
        }
    }

    fun getUserInfoFromToken(token: String): Claims {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).body
    }

    fun getUserId(token: String): CustomClaims {
        val claims = getUserInfoFromToken(token)
        val userId = (claims["id"] as? Number)?.toLong() ?: throw IllegalArgumentException("Invalid id format")
        return CustomClaims.of(userId, listOf("ROLE_USER"))
    }

    enum class TokenStatus {
        VALID,
        INVALID,
        EXPIRED
    }
}
