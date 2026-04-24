package com.learner.language.system.exception

import com.learner.language.application.validation.ValidationFailedException
import com.learner.language.application.validation.ValidationTimeoutException
import com.learner.language.domain.auth.OAuthAuthenticationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(BadRequestException::class)
    fun badRequestException(e: BadRequestException): ResponseEntity<ExceptionResponse> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ExceptionResponse(e.message ?: "잘못된 요청입니다", e.publicCode))

    @ExceptionHandler(ServiceUnavailableException::class)
    fun serviceUnavailableException(e: ServiceUnavailableException): ResponseEntity<ExceptionResponse> =
        ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(ExceptionResponse(e.message ?: "외부 서비스 호출에 실패했습니다", e.publicCode))

    @ExceptionHandler(OAuthAuthenticationException::class)
    fun oauthAuthenticationException(e: OAuthAuthenticationException): ResponseEntity<ExceptionResponse> {
        val status = when (e.oauthErrorCode) {
            ErrorCode.OAUTH_EMAIL_CONFLICT -> HttpStatus.CONFLICT
            ErrorCode.OAUTH_PROVIDER_ERROR -> HttpStatus.BAD_GATEWAY
            ErrorCode.OAUTH_CONFIGURATION_MISSING -> HttpStatus.INTERNAL_SERVER_ERROR
            else -> HttpStatus.BAD_REQUEST
        }
        return ResponseEntity.status(status)
            .body(ExceptionResponse(e.message ?: e.oauthErrorCode.name, e.publicCode))
    }

    @ExceptionHandler(NotFoundException::class)
    fun notFoundEx(e: NotFoundException): ResponseEntity<ExceptionResponse> =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ExceptionResponse(e.message ?: "리소스를 찾을 수 없습니다.", e.publicCode))

    @ExceptionHandler(ForbiddenException::class)
    fun forbiddenEx(e: ForbiddenException): ResponseEntity<ExceptionResponse> =
        ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ExceptionResponse(e.message ?: "접근 권한이 없습니다.", e.publicCode))

    @ExceptionHandler(UnprocessableEntityException::class)
    fun unprocessableEx(e: UnprocessableEntityException): ResponseEntity<ExceptionResponse> =
        ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(ExceptionResponse(e.message ?: "요청을 처리할 수 없습니다.", e.publicCode))

    @ExceptionHandler(ValidationTimeoutException::class)
    fun validationTimeout(e: ValidationTimeoutException): ResponseEntity<ExceptionResponse> =
        ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
            .body(ExceptionResponse(e.message ?: "AI 응답 지연", e.publicCode))

    @ExceptionHandler(ValidationFailedException::class)
    fun validationFailed(e: ValidationFailedException): ResponseEntity<ExceptionResponse> =
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ExceptionResponse(e.message ?: "AI 검증 실패", e.publicCode))

    @ExceptionHandler(LanguageException::class)
    fun languageEx(e: LanguageException): ResponseEntity<ExceptionResponse> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ExceptionResponse(e.message ?: "잘못된 요청입니다", e.publicCode))

    @ExceptionHandler(RuntimeException::class)
    fun runtimeEx(e: RuntimeException): ResponseEntity<ExceptionResponse> =
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ExceptionResponse("예측하지 못한 예외가 발생하였습니다. ${e.message}", "INTERNAL_SERVER_ERROR"))
}
