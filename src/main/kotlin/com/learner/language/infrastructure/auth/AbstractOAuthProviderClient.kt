package com.learner.language.infrastructure.auth

import com.learner.language.application.auth.OAuthProviderClient
import com.learner.language.application.auth.OAuthUserInfo
import com.learner.language.domain.auth.OAuthAuthenticationException
import com.learner.language.system.exception.ErrorCode
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientResponseException

abstract class AbstractOAuthProviderClient(
    private val provider: OAuthProperties.Provider,
    private val restClient: RestClient,
) : OAuthProviderClient {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun fetchUserInfo(authorizationCode: String): OAuthUserInfo {
        val accessToken = exchangeCodeForToken(authorizationCode)
        val rawUserInfo = fetchRawUserInfo(accessToken)
        return mapToUserInfo(rawUserInfo)
    }

    protected abstract fun mapToUserInfo(rawUserInfo: Map<String, Any?>): OAuthUserInfo

    private fun exchangeCodeForToken(code: String): String {
        val body = LinkedMultiValueMap<String, String>().apply {
            add("grant_type", "authorization_code")
            add("client_id", provider.clientId)
            add("client_secret", provider.clientSecret)
            add("redirect_uri", provider.redirectUri)
            add("code", code)
        }

        val response = try {
            restClient.post()
                .uri(provider.tokenUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .body(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError) { _, res ->
                    val errorBody = res.body.bufferedReader().use { it.readText() }
                    log.warn("OAuth token exchange failed status={} body={}", res.statusCode, errorBody)
                    throw OAuthAuthenticationException(
                        ErrorCode.OAUTH_PROVIDER_ERROR,
                        "OAuth token exchange failed: ${res.statusCode}",
                    )
                }
                .body(Map::class.java)
        } catch (e: OAuthAuthenticationException) {
            throw e
        } catch (e: RestClientResponseException) {
            log.warn("OAuth token exchange RestClient error status={} body={}", e.statusCode, e.responseBodyAsString)
            throw OAuthAuthenticationException(
                ErrorCode.OAUTH_PROVIDER_ERROR,
                "OAuth token exchange failed",
            )
        } catch (e: Exception) {
            log.warn("OAuth token exchange transport error: {}", e.message)
            throw OAuthAuthenticationException(
                ErrorCode.OAUTH_PROVIDER_ERROR,
                "OAuth token exchange transport failure",
            )
        }

        val accessToken = response?.get("access_token") as? String
            ?: throw OAuthAuthenticationException(
                ErrorCode.OAUTH_PROVIDER_ERROR,
                "OAuth token response missing access_token",
            )
        return accessToken
    }

    @Suppress("UNCHECKED_CAST")
    private fun fetchRawUserInfo(accessToken: String): Map<String, Any?> {
        val response = try {
            restClient.get()
                .uri(provider.userInfoUri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .onStatus(HttpStatusCode::isError) { _, res ->
                    val errorBody = res.body.bufferedReader().use { it.readText() }
                    log.warn("OAuth userinfo failed status={} body={}", res.statusCode, errorBody)
                    throw OAuthAuthenticationException(
                        ErrorCode.OAUTH_PROVIDER_ERROR,
                        "OAuth userinfo failed: ${res.statusCode}",
                    )
                }
                .body(Map::class.java)
        } catch (e: OAuthAuthenticationException) {
            throw e
        } catch (e: RestClientResponseException) {
            log.warn("OAuth userinfo RestClient error status={} body={}", e.statusCode, e.responseBodyAsString)
            throw OAuthAuthenticationException(
                ErrorCode.OAUTH_PROVIDER_ERROR,
                "OAuth userinfo failed",
            )
        } catch (e: Exception) {
            log.warn("OAuth userinfo transport error: {}", e.message)
            throw OAuthAuthenticationException(
                ErrorCode.OAUTH_PROVIDER_ERROR,
                "OAuth userinfo transport failure",
            )
        }

        return (response as? Map<String, Any?>)
            ?: throw OAuthAuthenticationException(
                ErrorCode.OAUTH_PROVIDER_ERROR,
                "OAuth userinfo body is empty",
            )
    }
}
