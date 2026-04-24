---
id: PRD-oauth-social-login
title: OAuth 소셜 로그인 (Kakao, Naver)
status: Draft
owner: jonghuncu@gmail.com
created: 2026-04-24
updated: 2026-04-24
related-adr: ../adr/0001-oauth-token-delivery-pattern.md
---

# PRD — OAuth 소셜 로그인 (Kakao, Naver)

> **읽기 전 필수**: 본 PRD 의 토큰 전달 방식 결정 근거는 `docs/adr/0001-oauth-token-delivery-pattern.md`
> 에 별도 ADR 로 분리되어 있다. 구현 전 ADR 을 먼저 읽고 결정의 트레이드오프를 숙지할 것.

---

## 1. 목표

- 카카오·네이버 OAuth 2.0 Authorization Code Grant 기반 소셜 로그인을 신규 도입한다.
- 백엔드는 콜백 후 **1회용 authorization code** 만 프론트로 전달하고, 별도 교환 endpoint
  로 access/refresh JWT 를 응답한다 (ADR-0001 결정).
- 모든 인증된 API 호출은 기존과 동일하게 `Authorization: Bearer ...` 표준을 따른다.
- 기존 이메일 가입 사용자(`User`) 모델을 확장 재사용한다 (별도 테이블/Aggregate 분리하지
  않음).

## 2. 비목표

- 카카오·네이버 외 provider (Google, Apple 등) — 후속 PRD.
- 소셜 계정 연결/해제(Link/Unlink) UI/UX — 단계 2.
- BFF 패턴 또는 HttpOnly 세션 쿠키 도입.
- Refresh token rotation 의 탈취 탐지(reuse detection) 정책 — 단계 2.
- Mobile-specific callback URL (custom scheme / App Link) 화이트리스트 — 모바일 앱 합류
  직전 별도 PRD 로 다룬다.

## 3. 배경

`language` 백엔드는 현재 이메일 + 비밀번호 가입/로그인만 지원한다. 사용자 진입 장벽을
낮추기 위해 국내 사용자 점유율이 높은 카카오·네이버 OAuth 를 추가한다.

현 인증 인프라:

- `system/security/JwtUtil.kt` — JWT 생성/검증.
- `system/security/JwtAuthorizationFilter.kt` — Bearer 토큰 인증 필터.
- `domain/user/User.kt` — JPA `@Entity`. (본 프로젝트는 도메인에 JPA 어노테이션을 직접
  부착하는 규약을 사용한다 — kidsinsightlab 의 strict DDD 와 다름.)
- `domain/user/RefreshToken.kt` + `infrastructure/user/UserRefreshTokenRepository.kt`.
- Reader/Writer 패턴: 도메인 인터페이스 → 인프라 구현 (`UserReader`/`UserReaderImpl`).
- 응용 계층: `application/user/UserFacade.kt` 와 같은 Facade.
- 컨트롤러: `interfaces/user/UserApiController.kt` 등 `interfaces/{domain}/` 위치.

본 PRD 는 위 규약을 그대로 따른다.

## 4. 사용자 시나리오

1. **신규 소셜 가입**: 사용자가 웹에서 "카카오로 시작하기" 클릭 → 카카오 로그인 페이지 →
   동의 → 앱으로 복귀 → 자동 가입 + 즉시 로그인 상태.
2. **기존 소셜 로그인**: 이미 카카오로 가입한 사용자가 동일 흐름으로 로그인. 신규 가입
   처리 없음.
3. **이메일 충돌**: 이미 이메일 `foo@bar.com` 으로 LOCAL 가입한 사용자가 동일 이메일을
   가진 카카오 계정으로 로그인 시도. **거부** (`OAUTH_EMAIL_CONFLICT`) — 자동 연결은
   피싱 위험으로 단계 1 에서 금지. 사용자에게 "기존 이메일 로그인 사용" 안내.
4. **provider 동의 항목 누락**: 사용자가 이메일 제공에 동의하지 않음 → `OAUTH_EMAIL_REQUIRED`
   에러로 동의 재요청 유도.
5. **로그아웃**: 클라이언트가 `POST /api/v1/auth/logout` 호출 → 서버는 refresh token
   무효화. access token 은 짧은 TTL 로 자연 만료.

## 5. 흐름

```
[Web]                          [Backend]                   [Provider]
  |--GET /api/v1/auth/oauth/{p}/start-->|                     |
  |   <302 Location + state cookie>     |                     |
  |--------- authorize ---------------------------------->    |
  |<--------- 302 callback?code&state -----------------------|
  |--GET /api/v1/auth/oauth/{p}/callback?code&state-->|      |
  |                                  |--state 쿠키 검증     |
  |                                  |--POST token---------->|
  |                                  |<---access_token-------|
  |                                  |--GET userinfo-------->|
  |                                  |<---profile------------|
  |                                  |--User upsert (DB)     |
  |                                  |--JWT 발급(access+ref) |
  |                                  |--AuthCode 저장 (TTL=60s) |
  |   <302 ${frontend}/auth/callback?code=AUTH_CODE>         |
  |--POST /api/v1/auth/exchange {code}->|                    |
  |                                  |--code consume(atomic) |
  |   <200 {accessToken,refreshToken,tokenType,expiresIn}>   |
  |                                                           |
  |--이후 모든 호출: Authorization: Bearer <access>           |
```

실패 분기:

- provider 가 `error` 반환 / code 누락 → `302 ${frontend}/auth/error?code=OAUTH_AUTHORIZATION_FAILED`
- state 쿠키 누락/불일치 → `302 ${frontend}/auth/error?code=OAUTH_STATE_INVALID`
- userinfo 에 email 없음 → `302 ${frontend}/auth/error?code=OAUTH_EMAIL_REQUIRED`
- 이메일 충돌 → `302 ${frontend}/auth/error?code=OAUTH_EMAIL_CONFLICT`
- provider 5xx/timeout → `302 ${frontend}/auth/error?code=OAUTH_PROVIDER_ERROR`
- exchange 시 code 만료/재사용 → `400 OAUTH_CODE_INVALID`

## 6. 기능 요구사항 (FR)

| FR# | 내용 |
|---|---|
| FR-1 | `GET /api/v1/auth/oauth/{provider}/start` — provider 인증 페이지로 302. state 쿠키 발급. |
| FR-2 | `GET /api/v1/auth/oauth/{provider}/callback` — code/state 검증, provider 토큰·userinfo 호출, User upsert, JWT 발급, 1회용 code 발급, frontend 로 302. |
| FR-3 | `POST /api/v1/auth/exchange` — 1회용 code → access/refresh JWT 응답. |
| FR-4 | `User` 도메인에 provider 식별자 필드 추가 (`provider`, `providerExternalId`). |
| FR-5 | 신규 OAuth 가입 시 `User` 자동 생성 (email, username, provider, providerExternalId, role=USER). |
| FR-6 | 기존 (provider, externalId) 매치 시 재로그인 처리. displayName 변경 시 update. |
| FR-7 | 이메일 충돌 시 `OAUTH_EMAIL_CONFLICT` 거부. |
| FR-8 | provider userinfo 에 email 누락 시 `OAUTH_EMAIL_REQUIRED` 거부. |
| FR-9 | state 쿠키 검증 (CSRF 방어). 쿠키 TTL 5분. |
| FR-10 | AuthCode TTL 60초, 1회 consume 후 즉시 삭제 (원자적). |
| FR-11 | `POST /api/v1/auth/logout` — 인증 사용자의 refresh token 삭제. |
| FR-12 | provider 호출 타임아웃 (connect 5s, read 10s). 실패 시 `OAUTH_PROVIDER_ERROR`. |
| FR-13 | provider properties 검증 — 누락 시 startup 실패 (`OAUTH_CONFIGURATION_MISSING`). |

## 7. 환경 설정 (`application.yml`)

```yaml
app:
  frontend-base-url: ${FRONTEND_BASE_URL:http://localhost:3000}
  backend-base-url:  ${BACKEND_BASE_URL:http://localhost:8080}
  jwt:
    secret: ${JWT_SECRET}                       # 기존 사용 — 변경 없음
    access-token-ttl: PT1H                      # (신규) 명시적 TTL 추가 — JwtUtil 의 5d 상수 대체 검토
    refresh-token-ttl: P14D
  auth:
    auth-code:
      ttl: PT60S
    oauth:
      kakao:
        client-id: ${KAKAO_CLIENT_ID}
        client-secret: ${KAKAO_CLIENT_SECRET}
        redirect-uri: ${app.backend-base-url}/api/v1/auth/oauth/kakao/callback
        authorize-uri: https://kauth.kakao.com/oauth/authorize
        token-uri: https://kauth.kakao.com/oauth/token
        user-info-uri: https://kapi.kakao.com/v2/user/me
        scope: account_email profile_nickname
      naver:
        client-id: ${NAVER_CLIENT_ID}
        client-secret: ${NAVER_CLIENT_SECRET}
        redirect-uri: ${app.backend-base-url}/api/v1/auth/oauth/naver/callback
        authorize-uri: https://nid.naver.com/oauth2.0/authorize
        token-uri: https://nid.naver.com/oauth2.0/token
        user-info-uri: https://openapi.naver.com/v1/nid/me
        scope: name email
```

- 모든 secret 은 환경변수. `.env.local` / `.env.example` 에 키만 추가 (값 비움).
- redirect-uri 는 provider 콘솔 등록 값과 정확히 일치해야 한다.

## 8. 디렉토리 구조 / 신규 파일 목록

본 프로젝트의 layered 구조와 Reader/Writer/Facade 컨벤션을 따른다.

```
src/main/kotlin/com/learner/language/
  domain/user/
    User.kt                                # [수정] provider, providerExternalId 필드 추가
    AuthProvider.kt                        # [신규] enum LOCAL, KAKAO, NAVER
    UserReader.kt                          # [수정] findByProvider(...) 추가
    UserWriter.kt                          # (변경 없음 — save 재사용)
  domain/auth/                             # [신규 패키지]
    OAuthAuthenticationException.kt        # [신규]
    AuthCode.kt                            # [신규] VO: code, AuthTokenSnapshot, expiresAt
    AuthTokenSnapshot.kt                   # [신규] code 저장용 토큰 직렬화 형태
  application/auth/                        # [신규 패키지]
    OAuthFacade.kt                         # [신규] login(provider, code, state) → authCode
    AuthExchangeFacade.kt                  # [신규] exchange(code) → AuthTokenResult
    OAuthCommand.kt                        # [신규] LoginCommand, ExchangeCommand
    OAuthProviderClient.kt                 # [신규] domain port (provider 추상)
    OAuthUserInfo.kt                       # [신규] provider 응답 통합 모델
    AuthCodeStore.kt                       # [신규] port: issue/consume
    AuthTokenIssuer.kt                     # [신규] port: User → AuthTokenResult
    AuthTokenResult.kt                     # [신규] accessToken, refreshToken, tokenType, expiresIn
  infrastructure/auth/                     # [신규 패키지]
    OAuthProperties.kt                     # [신규] @ConfigurationProperties("app.auth.oauth")
    AuthCodeProperties.kt                  # [신규] @ConfigurationProperties("app.auth.auth-code")
    KakaoOAuthProviderClient.kt            # [신규]
    NaverOAuthProviderClient.kt            # [신규]
    AbstractOAuthProviderClient.kt         # [신규] 공통 토큰/userinfo 호출
    KakaoOAuthUserInfo.kt                  # [신규]
    NaverOAuthUserInfo.kt                  # [신규]
    InMemoryAuthCodeStore.kt               # [신규] ConcurrentHashMap 기반 (단계 1)
    JwtAuthTokenIssuer.kt                  # [신규] AuthTokenIssuer 구현, JwtUtil 위임
  interfaces/auth/                         # [신규 패키지]
    OAuthBrowserController.kt              # [신규] start, callback
    AuthExchangeController.kt              # [신규] POST /exchange
    AuthDto.kt                             # [신규] ExchangeRequest, ExchangeResponse
    AuthLogoutController.kt                # [신규] POST /logout (별도 분리)
  system/security/
    OAuthStateCookieManager.kt             # [신규] state 쿠키 전용 (HttpOnly, SameSite=Lax)
    SecurityConfig.kt                      # [수정] 신규 endpoint permitAll 등록
  system/exception/
    GlobalExceptionHandler.kt              # [수정] OAuth ErrorCode 매핑
```

테스트는 `src/test/kotlin/.../` 동일 구조.

## 9. 도메인 변경 (`domain/`)

### 9.1 `AuthProvider` enum
```
LOCAL, KAKAO, NAVER
```

### 9.2 `User` 수정
- 신규 필드:
  ```kotlin
  @Enumerated(EnumType.STRING)
  @Column(name = "provider", nullable = false, length = 20)
  var provider: AuthProvider = AuthProvider.LOCAL,

  @Column(name = "provider_external_id", length = 100)
  var providerExternalId: String? = null,
  ```
- 기존 `password: UserPassword` 는 LOCAL 전용. OAuth 가입 시에는 사용하지 않는 더미 값
  허용 여부를 결정 필요 (Open Question Q1).
- 정적 팩토리 / `UserService` 에 OAuth 신규 가입 메서드 추가
  (`createOAuthUser(email, username, provider, externalId): User`).
- 불변식 (애플리케이션 단에서 검증, JPA 레벨 X):
  - `provider == LOCAL` → `password` 필수
  - `provider != LOCAL` → `providerExternalId` 필수, `password` 미사용

### 9.3 DB 스키마 변경
프로젝트가 Flyway/Liquibase 를 도입했는지 PRD 작성 시점에 확인되지 않음 (Open Question
Q2). 확인 후 둘 중 하나:

- **Flyway 도입되어 있음** → `db/migration/V{n}__add_user_oauth_columns.sql` 생성:
  ```sql
  ALTER TABLE "user"
    ADD COLUMN provider VARCHAR(20) NOT NULL DEFAULT 'LOCAL',
    ADD COLUMN provider_external_id VARCHAR(100);
  CREATE UNIQUE INDEX uk_user_provider_external
    ON "user"(provider, provider_external_id)
    WHERE provider <> 'LOCAL';
  ```
- **`ddl-auto=update` 만 사용 중** → entity 변경만으로 자동 반영. 운영 반영 전 수동
  ALTER 스크립트 PR 노트에 첨부.

### 9.4 `UserReader` 메서드 추가
```kotlin
fun findByProvider(provider: AuthProvider, externalId: String): User?
```
구현은 `infrastructure/user/UserReaderImpl.kt` + `UserRepository` (Spring Data JPA)
에 derived query 추가.

### 9.5 `OAuthAuthenticationException`
- provider 응답 검증 실패 시 throw. ErrorCode 매핑은 application/interfaces 계층에서.

### 9.6 `AuthCode`, `AuthTokenSnapshot` (도메인 VO)
- `AuthCode(value: String, snapshot: AuthTokenSnapshot, expiresAt: Instant)`.
- `AuthTokenSnapshot(accessToken, refreshToken, tokenType, expiresInSeconds)` — code 저장
  형태이며 외부 노출 시에는 `AuthTokenResult` 로 변환.

## 10. 애플리케이션 계층 (`application/auth/`)

### 10.1 Port — `OAuthProviderClient`
```kotlin
interface OAuthProviderClient {
    fun supports(): AuthProvider
    fun fetchUserInfo(authorizationCode: String, redirectUri: String): OAuthUserInfo
}
```
Spring 이 `List<OAuthProviderClient>` 를 주입받고 `supports()` 로 lookup.

### 10.2 Port — `OAuthUserInfo`
```kotlin
interface OAuthUserInfo {
    val provider: AuthProvider
    val externalId: String
    val email: String
    val displayName: String
}
```

### 10.3 Port — `AuthCodeStore`
```kotlin
interface AuthCodeStore {
    fun issue(snapshot: AuthTokenSnapshot): String         // returns one-time code (UUID)
    fun consume(code: String): AuthTokenSnapshot?          // atomic remove
}
```

### 10.4 Port — `AuthTokenIssuer`
```kotlin
interface AuthTokenIssuer {
    fun issue(user: User): AuthTokenResult
}
```
구현 (`JwtAuthTokenIssuer`) 은 기존 `JwtUtil.createToken(...)` 위임 + refresh token 발급
(기존 `RefreshToken` 도메인 활용).

### 10.5 `OAuthFacade.login(LoginCommand)`
입력: `LoginCommand(provider, authorizationCode, state)` (state 검증은 Controller 책임).

처리:
1. provider lookup → `fetchUserInfo(...)`.
2. `userReader.findByProvider(provider, externalId)`:
   - 존재 → displayName 변경 시 update 후 반환.
   - 없음 → `userReader.findByEmail(email)`:
     - 존재 (어떤 provider 든) → throw `OAUTH_EMAIL_CONFLICT`.
     - 없음 → `userService.createOAuthUser(...)` 후 반환.
3. `authTokenIssuer.issue(user)` → `AuthTokenResult`.
4. `authCodeStore.issue(AuthTokenSnapshot.of(result))` → `String code`.
5. 반환: `code`.

### 10.6 `AuthExchangeFacade.exchange(ExchangeCommand)`
1. `authCodeStore.consume(command.code)` → `null` 이면 throw `OAUTH_CODE_INVALID`.
2. `AuthTokenSnapshot` → `AuthTokenResult` 변환 후 반환.

원자성: `consume` 단일 호출이 remove + return. 동시 호출 시 1개만 성공.

## 11. 인프라스트럭처 계층 (`infrastructure/auth/`)

### 11.1 `OAuthProperties` (`@ConfigurationProperties("app.auth.oauth")`)
- `kakao: Provider`, `naver: Provider`.
- `data class Provider(val clientId, val clientSecret, val redirectUri, val authorizeUri, val tokenUri, val userInfoUri, val scope)`.
- `@Validated` + `@field:NotBlank`.

### 11.2 `AbstractOAuthProviderClient`
- Spring 6.1+ `RestClient` 1개 보유. (이미 `spring-boot-starter-web` 사용 중이므로 별도
  의존성 추가 불필요. WebClient 도 후보지만 `RestClient` 가 동기 호출에 더 단순.)
- 공통:
  - `exchangeCodeForToken(code, redirectUri): OAuthAccessTokenResponse`
    — `application/x-www-form-urlencoded`, grant_type=authorization_code.
  - `fetchRawUserInfo(accessToken): Map<String, Any?>`.
- 타임아웃: connect 5s / read 10s (`HttpClient` 기반).
- 5xx, IOException, 4xx 모두 `OAuthAuthenticationException` 으로 변환.

### 11.3 `KakaoOAuthProviderClient`
- userinfo 매핑:
  - `externalId` ← `id` (Long → String)
  - `email` ← `kakao_account.email`
  - `displayName` ← `kakao_account.profile.nickname`
- 동의 항목 미선택 시 email 이 null → `IllegalStateException` → ErrorCode 매핑은
  application 계층에서 `OAUTH_EMAIL_REQUIRED` 로 변환.

### 11.4 `NaverOAuthProviderClient`
- userinfo 매핑:
  - `externalId` ← `response.id`
  - `email` ← `response.email`
  - `displayName` ← `response.name`
- 응답 wrapper `{ resultcode, message, response: {...} }` 처리.

### 11.5 `InMemoryAuthCodeStore` (단계 1)
- `ConcurrentHashMap<String, Entry>` 기반.
- `Entry(snapshot, expiresAt: Instant)`.
- `issue`: UUID v4 → put. UUID 충돌 (사실상 0) 시 1회 재시도.
- `consume`: `compute` 또는 `remove` 후 expiresAt 체크 (만료면 null 반환).
- 백그라운드 cleanup: `@Scheduled(fixedRate = 30s)` 로 만료 항목 제거.
- **운영 단일 인스턴스 한정**. 멀티 인스턴스 전 Redis 교체 (단계 2 마일스톤).

### 11.6 `JwtAuthTokenIssuer`
- `JwtUtil.createToken(userId, email)` 호출 — 결과 문자열에 `Bearer ` prefix 가 이미
  포함됨 (기존 동작). `AuthTokenResult.accessToken` 에는 **prefix 제거 후** 저장
  (응답 body 의 `tokenType` 으로 분리 명시).
- refresh token: 기존 `domain/user/RefreshToken.kt` + `UserRefreshTokenRepository`
  활용. 사용자별 1개 정책 / 다중 정책 여부는 기존 코드 컨벤션 확인 후 결정.
- `expiresInSeconds`: `app.jwt.access-token-ttl` 에서 계산.

## 12. 인터페이스 계층 (`interfaces/auth/`)

### 12.1 `OAuthBrowserController`

`@RestController @RequestMapping("/api/v1/auth/oauth")`

- `GET /{provider}/start` →
  - state 생성 (UUID), `OAuthStateCookieManager.set(...)`.
  - `authorize-uri?response_type=code&client_id&redirect_uri&state` 로 302.
- `GET /{provider}/callback?code&state&error` →
  1. error/code 누락 → 실패 redirect.
  2. state 쿠키 검증 → 불일치 시 실패 redirect (`OAUTH_STATE_INVALID`).
  3. `OAuthFacade.login(...)` → `authCode` 수령.
  4. 성공 → `302 ${frontend}/auth/callback?code={authCode}` (토큰 미포함).
  5. 실패 (`BusinessException`) → `302 ${frontend}/auth/error?code={errorCode}`.
  6. finally: state 쿠키 만료.

### 12.2 `AuthExchangeController`

`@RestController @RequestMapping("/api/v1/auth")`

- `POST /exchange` body `{ "code": "uuid" }` → 200 `{ accessToken, refreshToken, tokenType: "Bearer", expiresIn }`.
- 실패 400 `{ errorCode: "OAUTH_CODE_INVALID", message }`.
- 인증 불필요. CORS 허용.

### 12.3 `AuthLogoutController`

`@RestController @RequestMapping("/api/v1/auth")`

- `POST /logout` (`@LoginUser userId: Long`) → refresh token 삭제 → 204.

### 12.4 `AuthDto`
```kotlin
object AuthDto {
    data class ExchangeRequest(@field:NotBlank val code: String) {
        fun toCommand(): ExchangeCommand = ExchangeCommand(code)
    }
    data class ExchangeResponse(
        val accessToken: String,
        val refreshToken: String,
        val tokenType: String,
        val expiresIn: Long,
    ) {
        companion object {
            fun from(result: AuthTokenResult) = ExchangeResponse(
                result.accessToken, result.refreshToken, result.tokenType, result.expiresInSeconds,
            )
        }
    }
}
```

## 13. 시스템 (`system/`)

### 13.1 `OAuthStateCookieManager` (`system/security/`)
- `set(response, provider, value)` — HttpOnly, Secure, SameSite=Lax, Path=/, Max-Age=300.
- `get(request, provider): String?`.
- `clear(response, provider)`.
- 쿠키명: `LANGUAGE_OAUTH_STATE_{PROVIDER}`.
- access token 은 절대 다루지 않는다 (이름 그대로 state 전용).

### 13.2 `SecurityConfig` 수정
permitAll 에 추가:
- `GET  /api/v1/auth/oauth/*/start`
- `GET  /api/v1/auth/oauth/*/callback`
- `POST /api/v1/auth/exchange`

`POST /api/v1/auth/logout` 은 인증 필요.

CORS: `app.frontend-base-url` origin 화이트리스트. `allowCredentials=false`
(쿠키 전송 안 함, state 쿠키는 same-site redirect 라 무관).

### 13.3 `JwtAuthorizationFilter` 변경 없음
- 기존 Bearer 헤더 추출 그대로 동작.
- 본 PRD 가 새로 도입하는 access token 은 동일 `JwtUtil` 로 생성되므로 기존 필터 검증
  로직과 호환.

### 13.4 `GlobalExceptionHandler` 수정
- `BusinessException` (또는 본 프로젝트의 표준 예외) → ErrorCode 의 HTTP status 매핑.
- `OAuthAuthenticationException` → 502 + `OAUTH_PROVIDER_ERROR` (provider 원문 메시지
  로그만 남기고 응답에는 노출 금지).

## 14. 에러 코드

기존 ErrorCode enum (또는 이에 상응하는 표준) 에 추가:

| 코드 | HTTP | 의미 |
|---|---|---|
| `OAUTH_AUTHORIZATION_FAILED` | 400 | provider 가 error 반환 또는 code 누락 |
| `OAUTH_STATE_INVALID` | 400 | state 쿠키 누락 또는 불일치 (CSRF 의심) |
| `OAUTH_PROVIDER_ERROR` | 502 | provider 호출 실패 (5xx, 타임아웃, 4xx) |
| `OAUTH_EMAIL_REQUIRED` | 400 | provider 가 email scope 미동의 |
| `OAUTH_EMAIL_CONFLICT` | 409 | 동일 이메일이 다른 가입 경로(LOCAL 또는 다른 provider)로 이미 존재 |
| `OAUTH_CODE_INVALID` | 400 | exchange 시 code 가 없거나 만료/소비됨 |
| `OAUTH_CONFIGURATION_MISSING` | 500 | properties 누락 (startup 검증 실패) |

## 15. 테스트 요구사항

### 15.1 도메인 단위 테스트
- `User` OAuth 신규 가입 팩토리 / 불변식.
- `AuthProvider` enum.

### 15.2 애플리케이션 단위 테스트 (Mockito 또는 MockK)
- `OAuthFacade.login`:
  - 신규 가입 (provider 사용자 없음, 동일 이메일 없음).
  - 재로그인 (provider+externalId 매치).
  - displayName 변경 시 update.
  - LOCAL 사용자 이메일 충돌 → `OAUTH_EMAIL_CONFLICT`.
  - 다른 provider 이메일 충돌 → `OAUTH_EMAIL_CONFLICT`.
  - email 누락 → `OAUTH_EMAIL_REQUIRED`.
- `AuthExchangeFacade.exchange`:
  - 정상 교환.
  - 없는 code → `OAUTH_CODE_INVALID`.
  - 동일 code 동시 2회 호출 시 1회만 성공 (`InMemoryAuthCodeStore` 직접 사용 통합 테스트).

### 15.3 인프라스트럭처 테스트
- `KakaoOAuthProviderClient`, `NaverOAuthProviderClient` — `MockWebServer` (OkHttp) 또는
  WireMock 으로 정상/4xx/5xx/타임아웃/이메일 누락 시나리오.
- `InMemoryAuthCodeStore` — 동시성 (`Awaitility` + `ExecutorService`), TTL 만료, cleanup
  스케줄러.
- `@DataJpaTest` — `UserReader.findByProvider`, unique index 검증.

### 15.4 인터페이스 테스트 (`@WebMvcTest`)
- `OAuthBrowserController`:
  - `/start` 가 302 + state 쿠키 + Location 헤더.
  - `/callback` 성공 → Location 이 `${frontend}/auth/callback?code=...`, **응답 본문/헤더 어디에도 토큰 문자열 미포함** 검증.
  - `/callback` state 불일치 → 실패 redirect.
- `AuthExchangeController`:
  - 정상 200 + body 검증.
  - 없는 code → 400.

### 15.5 통합 테스트 (`@SpringBootTest` + 임베디드 DB)
- 전체 흐름: `/start` → mock provider → `/callback` → frontend redirect 의 `code` 추출
  → `/exchange` → 발급된 access token 으로 `/api/v1/users/me` 200.

## 16. 클라이언트 계약 (Web → 향후 Mobile 공통)

별도 클라이언트 PRD/문서로 인계할 핵심 계약:

- **시작**: `GET /api/v1/auth/oauth/{kakao|naver}/start` 로 브라우저 location 이동.
- **콜백 처리**: 백엔드가 `${frontend}/auth/callback?code={uuid}` 로 redirect 한다.
  클라이언트는 `code` 추출 후 즉시 `/exchange` 호출. (페이지 reload, 분석 스크립트
  실행 전에 처리할 것 — Referer 누출 위험은 적지만 좋은 습관.)
- **교환**: `POST /api/v1/auth/exchange` body `{ "code": "..." }`
  → `{ accessToken, refreshToken, tokenType: "Bearer", expiresIn }`.
- **이후 API**: 모든 인증 호출에 `Authorization: Bearer {accessToken}`.
- **에러 redirect**: `${frontend}/auth/error?code={ErrorCode}` — §14 에러 코드 표 참조.
- **CORS**: 프론트 origin 백엔드 화이트리스트 등록 필요. `credentials` 불필요.
- **토큰 저장 정책 권고**:
  - `accessToken`: **메모리 우선**. 새로고침 대비 fallback 으로 `sessionStorage` 까지 허용.
    `localStorage` **금지** (XSS 노출면 확대).
  - `refreshToken`: 동일 정책. 단계 2 에서 HttpOnly cookie 분리 검토 가능성 — 캡슐화하여
    저장 매체 교체 비용 최소화.

## 17. 마일스톤 (PR 분할)

CLAUDE.md 의 단위 PR 원칙에 따라 6개로 분할 제안. 각 PR 완료 시 `code-reviewer`
호출 (`security-reviewer` 가 별도 정의되어 있다면 PR3·PR5·PR6 에 추가 호출).

| PR | 범위 | 의존 |
|---|---|---|
| PR1 | Domain 변경: `AuthProvider`, `User` 필드, `UserReader.findByProvider`, DB 스키마. | — |
| PR2 | Application 포트/파사드 + 단위 테스트 (provider client, code store mock). | PR1 |
| PR3 | Infrastructure: `KakaoOAuthProviderClient`, `NaverOAuthProviderClient` + WireMock 통합 테스트. | PR2 |
| PR4 | Infrastructure: `InMemoryAuthCodeStore` + 동시성 테스트, `JwtAuthTokenIssuer`. | PR2 |
| PR5 | Interface: 컨트롤러 2종 + `OAuthStateCookieManager` + `@WebMvcTest`. | PR3, PR4 |
| PR6 | `SecurityConfig` 갱신 + `GlobalExceptionHandler` + e2e 통합 테스트. | PR5 |

## 18. 결정 필요 사항 (구현 착수 전)

| ID | 질문 | 권장 |
|---|---|---|
| D1 | OAuth 가입자의 `User.password` 처리 — 더미 값 vs nullable 변경 | nullable 변경 (`UserPassword?` 또는 컬럼 nullable). 기존 LOCAL 사용자에게는 영향 없음. |
| D2 | `User` 테이블 마이그레이션 — Flyway vs `ddl-auto=update` | Flyway 도입 권장 (운영 안전성). 미도입 시 본 PR 에서 도입은 별도 ADR. |
| D3 | Refresh token rotation 정책 — 사용자별 1개 vs 디바이스별 다중 | 단계 1 사용자별 1개 (기존 컨벤션 확인 후 결정). |
| D4 | `JwtUtil.createToken` 의 `Bearer ` prefix 포함 동작 | 신규 코드는 prefix 제거 후 응답 body 로 분리. 기존 호출처 영향 검토 필요. |

## 19. Open Questions

- Q1: 프로젝트가 Flyway/Liquibase 를 도입했는지 확인 필요. (구현 시 `build.gradle.kts`
  와 `src/main/resources/db/` 점검)
- Q2: `RefreshToken` 도메인의 사용자별 다중성 정책 (현재 코드의 entity 와 repository
  contract 점검 필요).
- Q3: 단계 2 에서 refresh-only HttpOnly cookie 하이브리드 도입 시 CORS/도메인 정책
  재설계 범위.
- Q4: 모바일 앱 합류 시 callback URL 화이트리스트 / Universal Link / App Link 별도 PRD
  필요.

---

**참고**:

- ADR-0001 `docs/adr/0001-oauth-token-delivery-pattern.md` — (a)/(b)/(c) 결정 근거.
- 기존 인증 코드: `system/security/JwtUtil.kt`, `JwtAuthorizationFilter.kt`,
  `domain/user/User.kt`, `application/user/UserFacade.kt`, `interfaces/user/UserApiController.kt`.
