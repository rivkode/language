package com.learner.language.interfaces.auth

import com.learner.language.application.auth.OAuthFacade
import com.learner.language.application.auth.OAuthLoginCommand
import com.learner.language.domain.auth.OAuthAuthenticationException
import com.learner.language.domain.user.AuthProvider
import com.learner.language.infrastructure.auth.AuthAppProperties
import com.learner.language.infrastructure.auth.OAuthProperties
import com.learner.language.system.exception.ErrorCode
import com.learner.language.system.security.OAuthStateCookieManager
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.UUID

@RestController
@RequestMapping("/api/v1/auth/oauth")
class OAuthBrowserController(
    private val oauthFacade: OAuthFacade,
    private val oauthProperties: OAuthProperties,
    private val appProperties: AuthAppProperties,
    private val stateCookieManager: OAuthStateCookieManager,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @GetMapping("/{provider}/start")
    fun start(
        @PathVariable("provider") providerName: String,
        response: HttpServletResponse,
    ): ResponseEntity<Void> {
        val provider = parseProvider(providerName)
        val providerConfig = providerConfig(provider)
        val state = UUID.randomUUID().toString()

        stateCookieManager.set(response, provider, state)

        val authorizeUrl = UriComponentsBuilder.fromUriString(providerConfig.authorizeUri)
            .queryParam("response_type", "code")
            .queryParam("client_id", providerConfig.clientId)
            .queryParam("redirect_uri", providerConfig.redirectUri)
            .queryParam("scope", providerConfig.scope)
            .queryParam("state", state)
            .encode()
            .build()
            .toUriString()

        return ResponseEntity.status(HttpStatus.FOUND)
            .header(HttpHeaders.LOCATION, authorizeUrl)
            .build()
    }

    @GetMapping("/{provider}/callback")
    fun callback(
        @PathVariable("provider") providerName: String,
        @RequestParam(name = "code", required = false) code: String?,
        @RequestParam(name = "state", required = false) state: String?,
        @RequestParam(name = "error", required = false) error: String?,
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): ResponseEntity<Void> {
        val provider = parseProvider(providerName)
        val storedState = stateCookieManager.get(request, provider)

        try {
            if (!error.isNullOrBlank() || code.isNullOrBlank()) {
                log.warn("OAuth provider returned error provider={} error={}", provider, error)
                return errorRedirect(ErrorCode.OAUTH_AUTHORIZATION_FAILED)
            }

            if (storedState.isNullOrBlank() || state.isNullOrBlank() || storedState != state) {
                log.warn("OAuth state mismatch provider={} cookieState={} requestState={}", provider, storedState, state)
                return errorRedirect(ErrorCode.OAUTH_STATE_INVALID)
            }

            val authCode = oauthFacade.login(OAuthLoginCommand(provider, code))
            val location = "${appProperties.frontendBaseUrl}/auth/callback?code=" +
                URLEncoder.encode(authCode, StandardCharsets.UTF_8)

            return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, location)
                .build()
        } catch (e: OAuthAuthenticationException) {
            return errorRedirect(e.oauthErrorCode)
        } finally {
            stateCookieManager.clear(response, provider)
        }
    }

    private fun parseProvider(name: String): AuthProvider {
        return runCatching { AuthProvider.valueOf(name.uppercase()) }
            .getOrElse {
                throw OAuthAuthenticationException(
                    ErrorCode.OAUTH_AUTHORIZATION_FAILED,
                    "지원하지 않는 OAuth provider 입니다: $name",
                )
            }.also {
                if (it == AuthProvider.LOCAL) {
                    throw OAuthAuthenticationException(
                        ErrorCode.OAUTH_AUTHORIZATION_FAILED,
                        "LOCAL provider 는 OAuth 흐름을 사용할 수 없습니다.",
                    )
                }
            }
    }

    private fun providerConfig(provider: AuthProvider): OAuthProperties.Provider = when (provider) {
        AuthProvider.KAKAO -> oauthProperties.kakao
        AuthProvider.NAVER -> oauthProperties.naver
        AuthProvider.GOOGLE -> oauthProperties.google
        AuthProvider.LOCAL -> throw OAuthAuthenticationException(
            ErrorCode.OAUTH_AUTHORIZATION_FAILED,
            "LOCAL provider 는 OAuth 흐름을 사용할 수 없습니다.",
        )
    }

    private fun errorRedirect(errorCode: ErrorCode): ResponseEntity<Void> {
        val location = "${appProperties.frontendBaseUrl}/auth/error?code=" +
            URLEncoder.encode(errorCode.name, StandardCharsets.UTF_8)
        return ResponseEntity.status(HttpStatus.FOUND)
            .header(HttpHeaders.LOCATION, location)
            .build()
    }
}
