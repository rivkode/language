---
id: ADR-0001
title: OAuth 콜백 토큰 전달 패턴
status: Accepted
date: 2026-04-24
deciders: jonghuncu@gmail.com
related-prd: ../prd/oauth-social-login.md
---

# ADR 0001 — OAuth 콜백 토큰 전달 패턴

## 상태

Accepted (2026-04-24)

## 컨텍스트

`language` 백엔드는 현재 이메일/비밀번호 기반 회원가입·로그인만 제공하며, JWT access token 을
`Authorization: Bearer ...` 헤더로 검증한다 (`system/security/JwtUtil.kt`, `JwtAuthorizationFilter.kt`).

신규로 카카오·네이버 소셜 로그인을 추가한다. 외부 provider 의 표준 OAuth 2.0 Authorization
Code Grant 흐름을 따르되, **콜백 이후 백엔드가 발급한 JWT 를 클라이언트에 어떻게 전달할
것인가** 를 결정해야 한다.

제약:

- **현재 클라이언트는 웹만**, **향후 모바일(Flutter 추정) 추가 예정**. 단일 백엔드가 두 클라이언트를
  모두 지원해야 한다.
- 기존 인증은 Bearer 헤더 표준이며, 클라이언트도 이 패턴을 따르고 있다.
- Refresh token 은 이미 도메인 모델 (`domain/user/RefreshToken.kt`) 과 저장소
  (`infrastructure/user/UserRefreshTokenRepository.kt`) 가 존재한다.
- 운영 환경에 Redis 는 도입되어 있지 않다. Kafka 는 사용 중.

## 결정

**(c) Authorization Code Exchange 패턴을 채택한다.**

OAuth 콜백을 처리한 백엔드는 JWT 를 직접 redirect URL 에 싣지 않고, **1회용 단명(short-lived)
authorization code** 만 프론트엔드 redirect URL 에 부착한다. 클라이언트는 별도 endpoint
(`POST /api/v1/auth/exchange`) 에 code 를 제출하여 access/refresh 토큰을 응답 body 로
수령한다.

```
provider callback → backend issues code → 302 redirect (?code=...)
                                       → client POST /auth/exchange {code}
                                       → 200 {accessToken, refreshToken, ...}
```

## 검토한 대안

### (a) HttpOnly Cookie

콜백 응답에 `Set-Cookie` 로 access token 쿠키를 굽고, 이후 모든 요청에 브라우저가 자동
첨부.

- ✅ XSS 시 토큰 탈취 불가 (JS 가 `document.cookie` 로 못 읽음).
- ✅ 클라이언트가 토큰 처리 코드를 작성할 필요 없음.
- ❌ **모바일 합류 시 재작업.** Flutter `Dio` 는 cookie jar 를 별도 설정해야 하고, 기존
  Bearer 표준과 충돌한다.
- ❌ Cross-origin (FE 와 API 도메인 분리) 시 `SameSite=None; Secure` + CORS
  `credentials: include` 등 설정 부담.
- ❌ CSRF 방어 별도 필요.

### (b) URL Query Param JWT

`/auth/callback?accessToken=...&refreshToken=...` 로 redirect.

- ✅ 구현 가장 단순.
- ❌ **RFC 9700 §4.3.2 가 명시적으로 금지.** 다음 경로로 토큰이 유출된다:
  - 브라우저 히스토리 / 세션 복원
  - Referer 헤더 (콜백 페이지가 외부 리소스 로드 시)
  - 서버/CDN/리버스 프록시 access log
  - 사용자 URL 공유 사고
- ❌ Production 권장 사례 부재.

### (c) Code Exchange — **채택**

- ✅ 토큰이 URL/히스토리/Referer/로그 어디에도 노출되지 않는다 (1회용 code 만 노출).
- ✅ 웹·모바일이 **완전히 동일한 endpoint** 를 사용한다. 모바일 합류 시 백엔드 변경 0.
- ✅ OAuth 2.0 Authorization Code Grant 의 표준 흐름과 동형 — 검증된 모델.
- ✅ 기존 Bearer 인증 인프라 (`JwtUtil`, `JwtAuthorizationFilter`) 를 그대로 재사용.
- ⚠️ 1회용 code 저장소 필요. 단계 1 은 in-memory `ConcurrentHashMap` 으로 시작하고,
  다중 인스턴스 운영 시점에 Redis 로 교체 (PRD §11 참조).
- ⚠️ 클라이언트가 토큰을 보관하므로 XSS 위험은 (a) 보다 크다. 단계 2 에서
  refresh token 만 HttpOnly cookie 로 분리하는 하이브리드 보강 가능 (Open Question).

## 결과 (Consequences)

### 긍정

- 웹·모바일 통합 인증 백엔드 1세트로 운영 가능.
- 토큰 노출면 최소화 (URL/로그/Referer 무영향).
- OAuth 표준 흐름이라 외부 감사(audit) 시 설명 비용 낮음.
- 기존 JWT 인프라 재사용으로 변경 범위가 OAuth 신규 코드에 한정됨.

### 부정

- HTTP 왕복 1회 추가 (callback → frontend → exchange).
- AuthCodeStore 라는 신규 추상화와 그 구현체(메모리 → Redis) 운영 책임.
- 클라이언트가 access token 을 메모리/저장소에 보관해야 하므로 XSS 방어가 클라이언트의
  책임으로 남는다 (CSP, 의존 라이브러리 검수 등).

### 위험 / 완화책

| 위험 | 완화책 |
|---|---|
| in-memory code store 는 다중 인스턴스에서 일관성 깨짐 | 단계 1 단일 인스턴스 한정. 멀티 인스턴스 전 Redis 도입을 PRD 단계 2 로 못 박음. |
| code 재사용 공격 | `consume()` 은 원자적 (메모리 단계: `ConcurrentHashMap.remove`, Redis 단계: `GETDEL`). TTL 60s. |
| code 추측 공격 | UUID v4 (122-bit 무작위), TTL 짧음 → 무차별 공간 무의미. |
| XSS 로 access token 탈취 | access TTL 짧게 (1h), refresh rotation, 클라이언트 CSP 강화. 단계 2 에서 refresh-only HttpOnly cookie 하이브리드 검토. |
| provider 응답에 email scope 미포함 | `OAUTH_EMAIL_REQUIRED` 로 사용자 친화 에러. 동의 재요청 유도. |

## 참고

- OAuth 2.0 Security Best Current Practice (RFC 9700, 2025) §4.3.2 — URL query 에 토큰 금지.
- OAuth 2.0 for Browser-Based Apps (IETF draft, BCP 후보) — SPA 토큰 처리 가이드라인.
- RFC 8252 — OAuth 2.0 for Native Apps (모바일 합류 단계에서 PKCE 도입 시 참조).
