package com.learner.language.system.exception

import com.learner.language.common.EnumType

enum class ErrorCode(
    private val value: String
): EnumType {
    // 400
    BAD_REQUEST("AF001"), // 잘못된 입력값
    INVALID_AUTH_INFO("AF002"), // 아이디 비밀번호 매치 안됨
    NEW_PASSWORD_EQUALS_PREVIOUS("AF003"), // 변경하려는 비밀번호가 이전과 같음
    OLD_PASSWORD_NOT_EQUALS_PREVIOUS("AF004"), // 비밀번호 입력값이 기존 비밀번호와 일치하지 않음
    // 401
    ACCESS_TOKEN_EXPIRED("AF101"), // 액세스 토큰 만료
    UN_AUTHENTICATION("AF102"), // 인증 안됨
    REFRESH_TOKEN_EXPIRED("AF103"), // 리프레시 토큰 만료
    NOT_EXISTS_REFRESH_TOKEN("AF104"), // 리프레시 토큰 없음
    // 403
    UN_AUTHORIZATION("AF301"), // 권한 없음
    // 404
    NOT_FOUND("AF401"), // 존재하지 않는 리소스
    // 409
    ALREADY_EXISTS("AF901"), // 이미 존재하는 리소스
    CONCURRENCY("AF902"), // 선착순 마감
    // OAuth
    OAUTH_AUTHORIZATION_FAILED("AF601"), // provider 가 error 반환 또는 code 누락
    OAUTH_STATE_INVALID("AF602"), // state 쿠키 누락 또는 불일치
    OAUTH_PROVIDER_ERROR("AF603"), // provider 호출 실패 (5xx, 타임아웃, 4xx)
    OAUTH_EMAIL_REQUIRED("AF604"), // provider 가 email scope 미동의
    OAUTH_EMAIL_CONFLICT("AF605"), // 동일 이메일이 다른 가입 경로로 이미 존재
    OAUTH_CODE_INVALID("AF606"), // exchange 시 code 가 없거나 만료/소비됨
    OAUTH_CONFIGURATION_MISSING("AF607"), // properties 누락 (startup 검증 실패)
    // 500
    SERVICE_UNAVAILABLE("AF998"), // 서비스 이용 불가
    INTERNAL_SERVER_ERROR("AF999"); // 서버 내부 에러

    override fun getName(): String = name

    fun getValue(): String = value
}
