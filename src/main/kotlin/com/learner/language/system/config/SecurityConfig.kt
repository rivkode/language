package com.learner.language.system.config

import com.learner.language.domain.user.auth.JwtAuthenticationProvider
import com.learner.language.infrastructure.user.UserRefreshTokenRepository
import com.learner.language.system.security.*
import com.learner.language.utils.CookieUtil
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import com.learner.language.system.config.CorsProperties
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
class SecurityConfig(
    private val jwtUtil: JwtUtil,
    private val userRefreshTokenRepository: UserRefreshTokenRepository,
    private val userDetailsService: UserDetailsServiceImpl,
    private val authenticationConfiguration: AuthenticationConfiguration,
    private val authenticationProvider: JwtAuthenticationProvider,
    private val cookieUtil: CookieUtil
) {

    @Bean
    fun customPasswordEncoder(): CustomPasswordEncoder {
        return BCryptCustomPasswordEncoder()
    }

    @Bean
    @Throws(Exception::class)
    fun authenticationManager(configuration: AuthenticationConfiguration): AuthenticationManager {
        return configuration.authenticationManager
    }

    @Bean
    @Throws(Exception::class)
    fun jwtAuthenticationFilter(): JwtAuthenticationFilter {
        val filter = JwtAuthenticationFilter(jwtUtil, userRefreshTokenRepository, cookieUtil)
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration))
        return filter
    }

    @Bean
    fun jwtAuthorizationFilter(): JwtAuthorizationFilter {
        return JwtAuthorizationFilter(jwtUtil, userDetailsService, authenticationProvider, userRefreshTokenRepository, cookieUtil)
    }

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(httpSecurity: HttpSecurity, corsFilter: CorsFilter): SecurityFilterChain {
        httpSecurity
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .logout { it.disable() }
            .rememberMe { it.disable() }
            .anonymous { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter::class.java)
            .addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter::class.java)
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)
            .headers { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers(
                    "/api/v1/users/refresh-token",
                    "/api/v1/users/login",
                    "/api/v1/users",
                    "/api/v1/users/validation-email",
                    "/api/v1/users/validation-number",
                    "/api/v1/auth/oauth/*/start",
                    "/api/v1/auth/oauth/*/callback",
                    "/api/v1/auth/exchange",
                    "/swagger-ui/**",
                    "/swagger-ui/index.html",
                    "/v3/api-docs/**",
                    "/api-docs/**",
                    "/audio/**",
                    "/welcome/**"
                ).permitAll()
                it.anyRequest().authenticated()
            }

        return httpSecurity.build()
    }

    @Bean
    fun corsFilter(corsProperties: CorsProperties): CorsFilter {
        val config = CorsConfiguration().apply {
            allowedOrigins = corsProperties.allowedOrigins
            allowCredentials = corsProperties.allowCredentials
            allowedMethods = corsProperties.allowedMethods
            allowedHeaders = corsProperties.allowedHeaders
            exposedHeaders = corsProperties.exposedHeaders
            maxAge = corsProperties.maxAge
        }
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)
        return CorsFilter(source)
    }
}
