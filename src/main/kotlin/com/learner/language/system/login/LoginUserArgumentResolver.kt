package com.learner.language.system.login

import com.learner.language.domain.user.auth.AuthAuthenticationException
import com.learner.language.domain.user.auth.JwtAuthentication
import org.springframework.core.MethodParameter
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer


@Component
class LoginUserArgumentResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        val isLoginUserAnnotation = parameter.getParameterAnnotation(LoginUser::class.java) != null
        val isLongClass = parameter.parameterType == Long::class.java

        return isLoginUserAnnotation && isLongClass
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Long {
        val authentication: Authentication? = SecurityContextHolder.getContext().authentication
        if (authentication == null || !authentication.isAuthenticated) {
            throw AuthAuthenticationException("인증되지 않은 사용자입니다.")
        }


        checkAuthenticated(authentication)

        if (authentication.principal !is JwtAuthentication) {
            throw AuthAuthenticationException("잘못된 인증 형식입니다: ${authentication.principal?.javaClass?.name}")
        }

        val jwtAuthentication = authentication.principal as JwtAuthentication

        val userId = jwtAuthentication.userId
        return userId
    }

    private fun checkAuthenticated(authentication: Authentication?) {
        if (authentication == null) {
            throw AuthAuthenticationException("인증되지 않은 사용자입니다.")
        }
    }
}
