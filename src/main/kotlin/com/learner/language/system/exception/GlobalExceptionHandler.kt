package com.learner.language.system.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(BadRequestException::class)
    fun badRequestException(e: BadRequestException): ResponseEntity<ExceptionResponse> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ExceptionResponse(e.message ?: "잘못된 요청입니다"))

    @ExceptionHandler(RuntimeException::class)
    fun runtimeEx(e: RuntimeException): ResponseEntity<ExceptionResponse> =
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ExceptionResponse("예측하지 못한 예외가 발생하였습니다. ${e.message}"))

    @ExceptionHandler(NotFoundException::class)
    fun notFoundEx(e: NotFoundException): ResponseEntity<ExceptionResponse> =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ExceptionResponse(e.message ?: "리소스를 찾을 수 없습니다."))
}