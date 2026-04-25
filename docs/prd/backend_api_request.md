# 백엔드 API 개발 요청서 — 3줄 일기 소셜 러닝 v1

Last updated: 2026-04-24
Prepared for: **JamoAI 백엔드 팀**
Status: **Frontend contract frozen** — Flutter 앱은 현재 mock 리포지토리로 아래 스펙의 엔드포인트들을 모두 호출하고 있으며, 본 문서의 JSON 모양 그대로 파싱한다. 백엔드가 여기와 다른 응답을 내면 프론트는 파싱 실패 → 화면 깨짐.

## 0. 배경 & 목표

- 기존 한국어 학습 앱이 **3줄 일기 기반 소셜 러닝 앱**으로 전면 피벗했다 (상세: `three_line_diary_prd.md`).
- 프론트엔드(Flutter, 웹 타겟) 4개 핵심 화면 구현 완료:
    1. 홈 일기 피드 (카드 마소너리) — `DiaryFeedScreen`
    2. 일기 상세 + 댓글 — `DiaryDetailScreen`
    3. 일기 작성 + AI 검증 — `DiaryDraftScreen`
    4. 로그인/회원가입 (이메일 + Naver/Kakao/Google OAuth) — `LoginWarmScreen`, `SignupWarmScreen`
- 각 도메인별로 `*RepositoryMockImpl`이 in-memory 로 동작 중. 백엔드 API가 준비되는 대로 `InjectionContainer` 에서 Real impl 로 1줄 교체하면 끝.
- 본 문서는 **mock을 실 API로 대체하기 위한 구현 스펙**이다.
- **인증 관련 일부 엔드포인트(`/users/login`, `/users`, email verify, `/users/me`, `/auth/oauth/{provider}/*`, `/auth/exchange`)는 이미 백엔드에 구현되어 정상 동작 중**. 본 문서는 완료 분을 포함해 전체 v1 스펙을 일관되게 정리한 것이다.

## 1. 기술 전제

### 1.1 Base URL
| 환경 | URL |
|---|---|
| 로컬 | `http://localhost:8080/api/v1` |
| 운영 | `https://jamoai.app/api/v1` |

모든 엔드포인트는 위 base 아래에 위치한다 (v1 prefix 포함).

### 1.2 인증
- **Bearer 토큰 방식**: `Authorization: Bearer {accessToken}` 헤더로 전달
- 토큰은 `/auth/exchange` (OAuth) 또는 `/users/login` (이메일 로그인)에서 발급
- 액세스 만료 시 `/auth/refresh`로 재발급 (Flutter 측 `AuthenticatedHttpClient`가 401 → refresh → retry 자동 수행)
- **인증 불필요 엔드포인트**:
    - `POST /users/login`
    - `POST /users` (회원가입)
    - `POST /users/validation-number`, `POST /users/validation-email` (이메일 코드)
    - `GET /auth/oauth/{provider}/start`
    - `POST /auth/exchange`
    - `POST /auth/refresh`

### 1.3 JSON 규약
- 모든 request/response 는 JSON (Content-Type: `application/json`; charset=utf-8)
- **필드명은 camelCase** (snake_case 금지 — Flutter 모델은 camelCase로 파싱)
- 시간은 **ISO 8601 UTC** (`"2026-04-24T10:30:00Z"` 또는 `"2026-04-24T10:30:00.000+00:00"`)
- 숫자형 ID는 `int64` 범위 (Flutter `int`)
- 문자열 enum 값은 모두 `lower_snake` (예: `ok`, `suggestion`, `error`)

### 1.4 페이지네이션
- **cursor-based** 로 통일 (offset 금지)
- Request: `?cursor=<opaque>&size=<int>`
- Response:
  ```json
  {
    "items": [...],
    "paging": {
      "nextCursor": "opaque-string-or-null",
      "hasNext": true
    }
  }
  ```
- `cursor` 는 백엔드가 결정한 opaque string (offset number, timestamp, ULID 등 무엇이든 OK). 프론트는 그대로 다시 전달한다.
- `size` 기본값: 피드 10, 댓글 20

### 1.5 공통 에러 응답
```json
{
  "message": "사람이 읽기 좋은 에러 메시지 (선택)",
  "code": "ERROR_CODE_CONSTANT"
}
```
- `message`: 프론트가 UI에 노출 가능한 문구 (한국어 OK)
- `code`: 프론트 분기용 상수 (예: `OAUTH_CODE_EXPIRED`, `DIARY_NOT_FOUND`)
- HTTP 상태코드 사용 가이드:
  | 코드 | 용도 |
  |---|---|
  | 400 | 요청 형식/값 오류 (validation 실패) |
  | 401 | 인증 토큰 없음/만료/위조 |
  | 403 | 인증은 됐으나 권한 없음 (타인 일기 삭제 시도 등) |
  | 404 | 리소스 없음 |
  | 409 | 충돌 (이메일 중복 등) |
  | 422 | 의미적 오류 (예: 3줄이 아닌 일기 게시) |
  | 429 | rate limit |
  | 500 | 서버 오류 |

### 1.6 CORS (웹 한정 필수)
Flutter 웹이 현재 `http://localhost:3000`에서 `http://localhost:8080`을 호출하므로 다음 설정 필요:
- `Access-Control-Allow-Origin: http://localhost:3000` (운영은 Flutter 웹 배포 도메인)
- `Access-Control-Allow-Credentials: false` (Bearer 토큰 방식이라 cookie 불필요)
- preflight 허용 메서드: `GET, POST, PUT, DELETE, PATCH`
- preflight 허용 헤더: `Authorization, Content-Type`

---

## 2. 인증 / OAuth

### 2.1 `GET /auth/oauth/{provider}/start`  [✅ 이미 구현됨]
소셜 OAuth 시작. Flutter가 `launchUrl(...)`로 브라우저 이동만 수행.

| 항목 | 값 |
|---|---|
| 인증 | **불필요** |
| path param | `provider` ∈ {`google`, `kakao`, `naver`} |
| 응답 | 302 리다이렉트 (provider 인가 URL) |
| 콜백 후 | `{frontendBaseUrl}/auth/callback?code={one-time-uuid}` 으로 302 |
| `code` TTL | 60초, 1회 소진 |

**frontend-base-url 설정값** (백엔드 환경변수):
- 로컬: `http://localhost:3000`
- 운영: Flutter 웹 배포 URL (미정)

---

### 2.2 `POST /auth/exchange`  [✅ 이미 구현됨]
1회용 code를 실제 JWT 토큰으로 교환.

| 항목 | 값 |
|---|---|
| 인증 | **불필요** |
| Request body | `{ "code": "<one-time-uuid>" }` |
| Success 200 | `{ "accessToken": "eyJ...", "refreshToken": "<uuid>", "tokenType": "Bearer", "expiresIn": 432000 }` |
| Error 400 | 만료/재사용된 code → `{ "code": "OAUTH_CODE_EXPIRED" }` |

**Frontend 호출 위치**: `OAuthApiService.exchange()` → `lib/src/data/datasources/oauth_api_service.dart`
**응답 파싱**: `OAuthTokenResponse.fromJson()` — `tokenType` 필드는 무시. `accessToken`, `refreshToken`, `expiresIn`만 사용.

---

### 2.3 `POST /auth/refresh`  [🆕 신규 — 프론트는 호출 준비 완료]
만료 직전/만료된 access token 재발급.

| 항목 | 값 |
|---|---|
| 인증 | **불필요** (refresh token만 필요) |
| Request body | `{ "refreshToken": "<uuid>" }` |
| Success 200 | `/auth/exchange`와 동일한 shape |
| Error 401 | refresh token 무효/만료 → `{ "code": "REFRESH_TOKEN_INVALID" }` |

**동작 계약**:
- 새 refresh token 발급 여부는 서버 정책. 발급하면 rotating, 생략하면 기존 것 유지 (프론트는 `refreshToken`이 response에 있으면 덮어쓰고, 없으면 기존 것 유지)
- 401 응답 시 프론트는 **자동 로그아웃** + `/login` 리다이렉트

**Frontend 호출 위치**: `AuthenticatedHttpClient._refreshOnce()` — 모든 `/api/**` 호출의 401 응답 시 자동 1회 시도

---

### 2.4 `POST /auth/logout`  [🆕 신규]
서버측 세션/토큰 무효화.

| 항목 | 값 |
|---|---|
| 인증 | **필요** (현재 Bearer) |
| Request body | 없음 (또는 `{}`) |
| Success 200/204 | 응답 바디 무관 (프론트가 무시) |

**프론트 동작**: 응답 상태 무관 로컬 토큰 클리어. 네트워크 실패여도 UI는 로그아웃 진행.

---

### 2.5 `POST /users/login`  [✅ 이미 구현됨]
이메일/비밀번호 로그인.

| 항목 | 값 |
|---|---|
| 인증 | **불필요** |
| Request body | `{ "email": "...", "password": "..." }` |
| Success 200 | 응답 헤더 `Authorization: Bearer <accessToken>` |
| Error 401 | `{ "code": "INVALID_CREDENTIALS" }` |

**현재 프론트 처리**: `AuthApiService.login()` → 응답 헤더에서 Bearer 추출해 `TokenManager.saveAccessToken()`.
**개선 제안** (선택): OAuth와 동일하게 **body에 `{accessToken, refreshToken, expiresIn}`** 로 반환하면 refresh 플로우가 이메일 로그인에도 적용된다. 헤더 방식 유지 시 이메일 사용자는 만료 시 재로그인 필요.

---

### 2.6 `POST /users`  [✅ 이미 구현됨]
회원가입.

| 항목 | 값 |
|---|---|
| 인증 | **불필요** |
| Request body | `{ "email": "...", "password": "...", "username": "..." }` |
| Success 200/201 | body 무관 |
| Error 409 | 이메일 중복 → `{ "code": "EMAIL_ALREADY_EXISTS" }` |
| Error 400 | 이메일 인증 안 됨 → `{ "code": "EMAIL_NOT_VERIFIED" }` |

**선행 조건**: `/users/validation-number` → `/users/validation-email` 로 이메일 검증 완료되어야 한다.

---

### 2.7 `POST /users/validation-number`, `POST /users/validation-email`  [✅ 이미 구현됨]
이메일 인증 코드 발송 및 확인. 현재 정상 동작하므로 스펙 생략.

---

## 3. 사용자

### 3.1 `GET /users/me`  [✅ 이미 구현됨, 필드 확인 필요]
현재 로그인한 사용자 정보.

| 항목 | 값 |
|---|---|
| 인증 | **필요** |
| Response 200 | 아래 참조 |

**Response shape (요청)**:
```json
{
  "id": 123,
  "userId": 123,
  "email": "user@example.com",
  "username": "Minji",
  "avatarUrl": "https://...",
  "provider": "google",
  "createdAt": "2026-04-01T00:00:00Z"
}
```

**필드 상세**:
| 필드 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `id` | int | ✅ | 유저 PK |
| `userId` | int | ✅ | `id`와 동일값 (기존 호환) |
| `email` | string | ✅ | |
| `username` | string | ✅ | 표시명 |
| `avatarUrl` | string? | ❌ | 프로필 사진 URL. 없으면 null/omit |
| `provider` | string | ❌ | `google`/`kakao`/`naver`/`email` |
| `createdAt` | string | ❌ | 가입 시각 |

**Frontend 호출 위치**: `AuthApiService.getUserInfo()` — 파싱은 `UserInfo.fromJson()` → `lib/src/data/models/user/user_info.dart`

---

### 3.2 `GET /users/{userId}`  [🆕 신규 — 프로필 화면(screen 5)용]
타인 프로필 조회. 공개 일기 작성자 프로필 페이지 진입 시 사용 예정.

| 항목 | 값 |
|---|---|
| 인증 | **필요** |
| Response 200 | `/users/me`와 동일 shape + 통계 |

**확장 필드**:
```json
{
  "id": 456,
  "userId": 456,
  "username": "Junho",
  "avatarUrl": "https://...",
  "provider": "kakao",
  "createdAt": "...",
  "diaryCount": 12,
  "followerCount": 45,
  "followingCount": 30,
  "isFollowing": false,
  "bio": "한국어 학습 중 🇰🇷"
}
```

**우선순위**: Medium (프로필 화면 구현 시)

---

## 4. 일기 (Diary)

### 4.1 `GET /diaries/feed`  [🆕 신규 — 핵심]
홈 피드의 공개 3줄 일기 목록.

| 항목 | 값 |
|---|---|
| 인증 | **필요** |
| Query params | `cursor` (string, optional), `size` (int, default 10), `sort` (`recent`\|`trending`, default `recent`), `category` (string, optional - 태그 필터) |

**Response 200**:
```json
{
  "items": [
    {
      "diaryId": 1,
      "author": {
        "userId": 101,
        "username": "Minji",
        "avatarUrl": null
      },
      "lines": [
        "오늘은 비가 와서 우산을 챙겼다.",
        "카페에서 따뜻한 라떼를 마셨다.",
        "창밖을 보며 책을 읽는 시간이 좋았다."
      ],
      "createdAt": "2026-04-24T07:30:00Z",
      "tags": ["#비오는날"],
      "likeCount": 12,
      "commentCount": 5,
      "voiceParticipantCount": 3,
      "userLiked": false,
      "isPublic": true,
      "accentTone": "cream"
    }
  ],
  "paging": {
    "nextCursor": "cursor-string-or-null",
    "hasNext": true
  }
}
```

**필드 상세**:
| 필드 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `diaryId` | int | ✅ | |
| `author.userId` | int | ✅ | |
| `author.username` | string | ✅ | |
| `author.avatarUrl` | string? | ❌ | 없으면 null |
| `lines` | string[3] | ✅ | **반드시 3개 요소** |
| `createdAt` | string | ✅ | ISO 8601 |
| `tags` | string[] | ❌ | 없으면 `[]` |
| `likeCount` | int | ✅ | |
| `commentCount` | int | ✅ | |
| `voiceParticipantCount` | int | ✅ | 현재 라이브 중인 음성방 참여자 수. 방 없으면 0 |
| `userLiked` | bool | ✅ | 요청자 기준 |
| `isPublic` | bool | ✅ | |
| `accentTone` | string | ❌ | `cream`\|`apricot`\|`mint`\|`lavender`\|`outline` 중 하나. 없으면 `cream`. 백엔드에서 저장하거나 diaryId 해시로 계산해도 됨 |

**Frontend 호출 위치**: `DiaryRepositoryMockImpl.getDiaryFeed()` → 실 API로 교체 대상. 파싱은 `DiaryFeedResponse.fromJson()`.

---

### 4.2 `GET /diaries/{diaryId}`  [🆕 신규]
단일 일기 상세 (홈 → 카드 탭 시).

| 항목 | 값 |
|---|---|
| 인증 | **필요** |
| path param | `diaryId` (int) |

**Response 200**: `feed` item과 **동일한 shape** (배열 아닌 단일 객체).
**Error 404**: `{ "code": "DIARY_NOT_FOUND" }`

---

### 4.3 `POST /diaries`  [🆕 신규 — 핵심]
새 일기 게시.

| 항목 | 값 |
|---|---|
| 인증 | **필요** |
| Request body | 아래 |

**Request**:
```json
{
  "lines": ["첫 문장", "두 번째 문장", "세 번째 문장"],
  "tags": ["#맑음"],
  "isPublic": true
}
```

**필드 제약**:
- `lines`: **반드시 3개**, 각 라인 1~200자
- `tags`: 0~5개, 각 1~32자
- `isPublic`: true = 공개 피드 노출, false = 작성자만 (프라이버시 칩 "Only Me"에 대응)
- **"Friends Only" 프라이버시는 v1 범위 밖** (친구 관계 스키마 미정의). 프론트는 `isPublic=false`로 보내고, 프로필 확장 시 재정의.

**Response 200/201**: 생성된 diary 전체 객체 (feed item shape).
**Error 422**: lines가 3개가 아니면 `{ "code": "INVALID_LINE_COUNT" }`
**Error 400**: 라인 비어있음 또는 너무 김 → `{ "code": "INVALID_LINE_LENGTH" }`

**Frontend 호출 위치**: `DiaryDraftViewModel.post()` → `CreateDiaryUseCase` → `DiaryRepositoryMockImpl.createDiary()`

---

### 4.4 `PUT /diaries/{diaryId}`  [🆕 신규 — 우선순위 낮음]
일기 수정. v1에는 본인만 수정 가능, 게시 후 5분 이내 등 정책은 백엔드 결정.

| Request body | `POST /diaries`와 동일 |
| Error 403 | 본인 아님 → `DIARY_FORBIDDEN` |

**우선순위**: Low. 현재 프론트에 수정 UI 없음.

---

### 4.5 `DELETE /diaries/{diaryId}`  [🆕 신규 — 우선순위 낮음]
일기 삭제.

| Success 200/204 | body 무관 |
| Error 403 | 본인 아님 |

**우선순위**: Low.

---

### 4.6 `GET /diaries/me`  [🆕 신규 — 프로필 화면용]
내가 쓴 일기 목록 (최신순).

| 인증 | 필요 |
| Query params | `cursor`, `size` (default 10) |
| Response | `/diaries/feed`와 동일 shape (본인 비공개 포함) |

**우선순위**: Medium (프로필 화면 작업 시).

---

### 4.7 `POST /diaries/{diaryId}/like`  [🆕 신규]
좋아요 토글.

| Request body | `{ "liked": true }` 또는 `{ "liked": false }` |
| Response 200 | `{ "diaryId": 1, "likeCount": 13, "userLiked": true }` |

**동작 계약**:
- 요청의 `liked` 값으로 **멱등 설정** (idempotent). true면 좋아요 on, false면 off.
- 이미 같은 상태여도 200 반환.

**Frontend 호출 위치**: `DiaryRepositoryMockImpl.toggleLike()` — 낙관적 업데이트 후 API 호출, 실패 시 롤백.

---

## 5. 댓글

### 5.1 `GET /diaries/{diaryId}/comments`  [🆕 신규]
특정 일기의 댓글 목록 (오래된 순 또는 최신순, 백엔드 결정).

| 인증 | 필요 |
| Query params | `cursor`, `size` (default 20) |

**Response 200**:
```json
{
  "items": [
    {
      "commentId": 1001,
      "diaryId": 1,
      "author": {
        "userId": 201,
        "username": "David",
        "avatarUrl": null
      },
      "text": "정말 좋네요!",
      "createdAt": "2026-04-24T08:00:00Z",
      "parentCommentId": null,
      "likeCount": 2,
      "userLiked": false
    }
  ],
  "paging": {
    "nextCursor": "...",
    "hasNext": false
  }
}
```

**필드 상세**:
| 필드 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `commentId` | int | ✅ | |
| `diaryId` | int | ✅ | 상위 일기 id |
| `author` | object | ✅ | |
| `text` | string | ✅ | 1~500자 |
| `createdAt` | string | ✅ | |
| `parentCommentId` | int? | ❌ | 대댓글일 때 상위 댓글 id. 최상위면 null/omit |
| `likeCount` | int | ✅ | |
| `userLiked` | bool | ✅ | |

**Frontend 호출 위치**: `DiaryCommentRepositoryMockImpl.getComments()` → 파싱 `DiaryCommentListResponse.fromJson()`

---

### 5.2 `POST /diaries/{diaryId}/comments`  [🆕 신규]
댓글 작성.

| Request body | `{ "text": "...", "parentCommentId": 1001 }` |
| Success 200/201 | 생성된 comment 객체 |
| Error 400 | 빈 text 또는 500자 초과 |
| Error 404 | 일기 없음 |

**동작**:
- `parentCommentId`가 있으면 대댓글, 없으면 최상위
- 일기의 `commentCount`는 백엔드가 자동 증가 (응답에 포함되진 않지만 다음 feed 조회에 반영)

---

### 5.3 `POST /comments/{commentId}/like`  [🆕 신규]
댓글 좋아요 토글.

| Request body | `{ "liked": true }` |
| Response 200 | `{ "commentId": 1001, "likeCount": 3, "userLiked": true }` |

---

### 5.4 `DELETE /comments/{commentId}`  [🆕 신규 — 우선순위 낮음]
댓글 삭제 (본인만).

**우선순위**: Low.

---

## 6. AI 검증 (3줄 일기 게시 전 검증)

### 6.1 `POST /diaries/validate`  [🆕 신규 — 핵심]
3줄 배열을 받아 per-line 검증 피드백 반환.

| 인증 | 필요 |
| Request body | `{ "lines": ["첫 문장", "두 번째 문장", "세 번째 문장"] }` |

**Request 제약**: 배열 길이 정확히 3, 각 라인 1~200자.

**Response 200** (동기):
```json
{
  "validationId": "val-abc123",
  "lines": [
    {
      "lineIndex": 0,
      "originalText": "오늘 아침에 일찍 일어나서 상쾌했다.",
      "status": "ok",
      "message": "Sounds natural!",
      "suggestion": null
    },
    {
      "lineIndex": 1,
      "originalText": "카페에 가서 커피 마셨다.",
      "status": "suggestion",
      "message": "How about fixing it like this?",
      "suggestion": "카페에서 따뜻한 라떼를 마셨어요."
    },
    {
      "lineIndex": 2,
      "originalText": "친구 만났어 즐거운 시간 보냈다.",
      "status": "error",
      "message": "'만났어' 대신 '만나서'로 연결하는 것이 자연스러워요.",
      "suggestion": "친구를 만나서 즐거운 시간을 보냈다."
    }
  ]
}
```

**필드 상세 (각 line object)**:
| 필드 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `lineIndex` | int | ✅ | 0/1/2 |
| `originalText` | string | ✅ | 요청 그대로 에코 |
| `status` | string | ✅ | `ok` \| `suggestion` \| `error` 중 하나 |
| `message` | string | ✅ | AI 피드백 문장. 다국어 OK (영/한 혼용) |
| `suggestion` | string? | ❌ | 대안 문장. `status`가 `ok`면 null/omit |

**status별 UI 처리**:
- `ok` → 민트 카드 + 체크 아이콘. 게시 가능.
- `suggestion` → 살구 카드 + 전구 아이콘 + suggestion 박스 + Apply 버튼. 게시 가능 (사용자가 원본 유지 가능).
- `error` → 코랄 카드 + 에러 아이콘. **이 상태면 게시 버튼 비활성화**. 사용자는 수정 후 재검증해야 함.

**성능 요구**:
- 동기 응답. 2초 이내 목표 (현재 프론트 mock은 600ms)
- 비동기(polling) 전환 필요 시 `202 Accepted { validationId }` + `GET /diaries/validate/{id}` 패턴 제안 (아래 6.2 참조). **v1은 동기 우선**.

**Frontend 호출 위치**: `DiaryValidationRepositoryMockImpl.validate()` → 파싱 `DiaryValidation.fromJson()`

---

### 6.2 `GET /diaries/validate/{validationId}`  [선택 — 비동기 전환 시]
검증 결과 polling (6.1이 비동기일 때만).

| Response 200 | `{ "status": "pending"\|"completed"\|"failed", "lines": [...] }` |

**우선순위**: Low. v1은 6.1 동기 응답으로 충분.

---

### 6.3 `POST /diaries/validate/line`  [🆕 신규 — per-line 재검증]
한 줄만 재검증 (사용자가 수정 후 "Re-verify" 탭 시).

| 인증 | 필요 |
| Request body | `{ "lineIndex": 1, "text": "수정된 문장" }` |
| Response 200 | 6.1의 단일 line object와 동일 shape |

**Frontend 호출 위치**: `DiaryValidationRepositoryMockImpl.validateLine()` → `RevalidateDiaryLineUseCase`

---

## 7. 음성 기반 텍스트 채팅방 (Screen 3 — ✅ 구현 완료)

Screen 3은 **"각자 녹음 → STT → 텍스트로 합류하는 음성 기반 텍스트 방"** 으로 구현한다. 실시간 음성 통화(라이브 보이스) 아님, WebRTC·SFU·시그널링 서버 **불필요**. 메시지 전달은 **롱 폴링** 사용 (실시간성이 핵심 가치가 아니므로 HTTP만으로 구성).

계약을 미리 잡아두면 다음 스프린트에 바로 들어갈 수 있다. ~~**우선순위: 백엔드 검토 후 v2 범위 결정.**~~ → **백엔드 v1 구현 완료 (Flyway V6, `feat/diary-chatroom-v1` PR).**

### 7.0 구현 결정 사항 (백엔드 회신)

이전 "결정 필요 항목"에 대한 백엔드 측 결정:

| # | 항목 | 결정 |
|---|---|---|
| 1 | AI 응답 생성 주체 | 기존 Spring AI `ChatClient` 재사용 + 채팅방 전용 prompt(`DiaryChatAiService`) 분리. 페르소나 기반 `/chat/generate` 는 재사용 X — 컨텍스트가 다름 |
| 2 | AI 응답 트리거 | **방 단위 토글 + 자동 응답** (default ON). 사용자 메시지(`source=user`)마다 AI가 자동 응답. AI 자기 메시지에는 반응 안 함 (loop 방지). 방 최초 생성 시 일기 3줄 본문을 컨텍스트로 **웰컴 메시지** 자동 생성 |
| 3 | 참여자 상한 | **방당 20명**. 초과 시 `CHATROOM_PARTICIPANT_LIMIT` 409 |
| 4 | 메시지 보존 | **90일** hard delete. 매일 04:00 UTC `@Scheduled` 배치 (`DiaryChatRetentionScheduler`) |
| 5 | POLL_CURSOR_EXPIRED 기준 | `after` 가 **7일 이상 경과**한 messageId 또는 보존 정책으로 삭제된 messageId 면 410 반환 |
| 6 | AI 응답 지연 최적화 | `ApplicationEventPublisher` + `@TransactionalEventListener(AFTER_COMMIT)` + `@Async` 로 비동기 생성. 시작 시 `ai_typing` 이벤트, 실패 시 `ai_failed` 이벤트가 같은 폴링 채널에 즉시 흐름 |
| 7 | 참여 권한 | v1 = **공개 일기에 연결된 방은 로그인 사용자 누구나 참여 가능**. 비공개 일기는 방 생성 자체 차단 (`DIARY_FORBIDDEN`) |

### 7.0.1 구현 메모 / spec 대비 diff

- **events 저장 모델 (구현 변경)** — spec은 events 를 별도 데이터로 다루지만, 단일 `after={messageId}` cursor 정합성을 위해 백엔드 내부에서는 `diary_chat_message` 테이블에 `source='system' + event_type` 형태로 함께 저장한다. 폴링 응답 시 `source` 로 분리해서 `items` / `events` 두 필드로 변환. **응답 shape 은 spec 그대로**.
- **AI 토글 기본값** — spec은 `aiAssistantEnabled` 기본 false. 사용자(=PRD owner) 결정으로 **default true** 로 변경 (방 생성 시 자동 ON). 방장이 끌 수 있음.
- **AI 식별자** — `author.userId = -1`, `username = "Jamo AI"` 합성 author 로 응답 (별도 필드 없이 author 객체에 담김).
- **단일 인스턴스 전제** — `DiaryChatPollingHub` 는 in-memory `ConcurrentHashMap<roomId, List<DeferredResult>>` 기반. 다중 인스턴스 배포 시 Redis Pub/Sub fan-out 필요 (v2 작업 항목).
- **방장 leave 정책** — spec 명시 없음. v1 = **방장은 leave 불가** (`CHATROOM_FORBIDDEN`). 방 삭제는 v2.
- **`audioUrl` 보관** — 메시지 컬럼 그대로 저장만 함. 백엔드 측 자동 TTS / 검증 없음.
- **`/chat/transcribe`, `/chat/speech` 재사용** — 본 채팅방 도메인은 이 두 엔드포인트와 직접 결합 없음. 클라이언트가 따로 호출.

### 7.1 주요 요구사항
- 각 공개 일기마다 1개의 채팅방이 "열릴 수 있음" (`diaryId`로 1:1 매핑)
- 여러 사용자가 동시 참여 가능 (N명)
- AI 비서 토글 (방장이 on/off). ON일 때 AI가 대화에 참여
- 음성 입력: 클라이언트가 로컬 녹음 → 녹음 완료 후 기존 `POST /chat/transcribe`로 업로드 → 반환된 텍스트를 방 메시지로 전송
- 메시지 TTS 재생: 기존 `POST /chat/speech` 재사용
- 메시지 전달: **롱 폴링** (WebSocket/SSE 사용 안 함)
- 서버는 원본 오디오를 방에 중계하지 않는다. 방에 흐르는 것은 **텍스트 메시지**만 (선택적으로 `audioUrl` 첨부).

### 7.2 제안 엔드포인트 (초안 v2)
```
POST   /chatrooms                               body: { diaryId, aiAssistantEnabled? }
                                                → 같은 diaryId로 재호출 시 기존 방 반환 (idempotent)
GET    /chatrooms/{id}                          방 메타데이터 (제목, 방장, aiAssistantEnabled, 참여자 수)
GET    /chatrooms/{id}/participants             현재 참여자 목록
POST   /chatrooms/{id}/ai-toggle                body: { enabled }  (방장만 가능)
POST   /chatrooms/{id}/join
POST   /chatrooms/{id}/leave
GET    /chatrooms/{id}/messages?before=&size=   과거 메시지 페이지네이션 (초기 진입/스크롤 업)
GET    /chatrooms/{id}/messages/poll?after={messageId}&wait={sec}
                                                롱 폴링 — 7.3 참조
POST   /chatrooms/{id}/messages                 body: { text, audioUrl? }
```

### 7.3 롱 폴링 동작 규약
- 엔드포인트: `GET /chatrooms/{id}/messages/poll?after={lastMessageId}&wait={sec}`
- `after`: 클라가 마지막으로 받은 messageId. 초기 진입 시 `GET /chatrooms/{id}/messages`로 히스토리를 먼저 로드하고, 그 마지막 id를 `after`에 넣어 폴링 시작.
- `wait`: 서버가 새 메시지 없을 때 대기할 최대 초. **권장 기본 25, 최대 60** (프록시/게이트웨이 타임아웃 고려).
- 새 메시지가 있으면 **즉시** 반환. 없으면 `wait`초 대기 후 빈 배열 반환.
- 응답 shape 제안:
  ```json
  {
    "items": [
      { "messageId": 42, "roomId": 7, "author": {...}, "text": "...", "audioUrl": null, "createdAt": "...", "source": "user" }
    ],
    "events": [
      { "type": "participant_joined", "userId": 101, "at": "..." },
      { "type": "ai_toggle_changed", "enabled": true, "at": "..." }
    ],
    "nextAfter": 42
  }
  ```
    - `items`: 새 메시지 배열 (없으면 `[]`)
    - `events`: 참여자 입/퇴장·AI 토글 변경 등 메타 이벤트 (없으면 `[]`). **메시지 스트림과 합치지 말고 별도 배열로** 분리 — 클라 ViewModel에서 분기하기 편함.
    - `nextAfter`: 클라가 다음 폴링 요청의 `after`로 넣어야 할 값. 새 메시지 없었으면 `after`와 동일하게 에코.
- `source` enum: `user` | `ai` | `system` (system은 향후 공지용 여유).
- 클라이언트 동작: 응답을 받자마자 **즉시** 다음 요청을 띄우는 루프. 실패 시 지수 백오프(1s → 2s → 4s, 최대 30s). 화면 이탈/앱 백그라운드 진입 시 진행 중 요청 취소.
- 서버 동작: `wait` 만료 시 **HTTP 200 + 빈 items**로 응답. `408 Request Timeout` 금지 (일반 에러와 섞이면 클라 재시도 로직이 복잡해짐).
- 재연결 시 `after` 갭이 너무 크면(예: N시간 이상) 히스토리 재로드 유도 → `{ "code": "POLL_CURSOR_EXPIRED" }` (8장에 추가 예정).

### 7.4 결정 필요 (업데이트 2026-04-25)
- ~~실시간 레이어: WebSocket vs SSE vs 폴링~~ → **롱 폴링 확정**
- ~~마이크 오디오 스트리밍: 서버 중계 vs WebRTC P2P~~ → **스트리밍 없음, 클라 녹음 후 `/chat/transcribe` 호출 확정**
- AI 응답 생성 주체: 기존 `/chat/generate` 재사용 vs 신규 채팅방 전용 엔드포인트 — **미결**
- AI 응답 트리거: 사용자 메시지마다 자동 생성 vs 멘션/명령어 기반 — **미결**
- 참여자 상한: 방당 최대 N명 — **미결** (폴링 부하에 영향)
- 메시지 보존 기간: 방이 "닫힌" 후에도 히스토리 조회 가능해야 하는가 — **미결**

**본 문서 v1 스코프 밖.** 7번 섹션은 v2 설계 참고용.

---

## 8. 에러 코드 사전

### 8.1 인증
| 코드 | HTTP | 의미 |
|---|---|---|
| `OAUTH_CODE_MISSING` | 400 | /auth/callback에 code 없음 |
| `OAUTH_CODE_EXPIRED` | 400 | 1회용 code 만료(60s) 또는 재사용 |
| `OAUTH_AUTHORIZATION_FAILED` | 400 | provider 단에서 실패 |
| `REFRESH_TOKEN_INVALID` | 401 | refresh token 무효/만료 |
| `INVALID_CREDENTIALS` | 401 | 이메일/비번 틀림 |
| `EMAIL_ALREADY_EXISTS` | 409 | 회원가입 중복 |
| `EMAIL_NOT_VERIFIED` | 400 | 이메일 미인증 회원가입 시도 |

### 8.2 일기 / 댓글
| 코드 | HTTP | 의미 |
|---|---|---|
| `DIARY_NOT_FOUND` | 404 | |
| `DIARY_FORBIDDEN` | 403 | 타인 일기 수정/삭제 시도 |
| `INVALID_LINE_COUNT` | 422 | lines가 3개가 아님 |
| `INVALID_LINE_LENGTH` | 400 | 빈 라인 또는 200자 초과 |
| `COMMENT_NOT_FOUND` | 404 | |
| `COMMENT_TOO_LONG` | 400 | 500자 초과 |

### 8.3 AI 검증
| 코드 | HTTP | 의미 |
|---|---|---|
| `VALIDATION_FAILED` | 500 | AI 모델 호출 실패 |
| `VALIDATION_TIMEOUT` | 504 | 응답 지연 |

### 8.4 채팅방 (§7 — v2 설계 참고용, v1 스코프 밖)
| 코드 | HTTP | 의미 |
|---|---|---|
| `CHATROOM_NOT_FOUND` | 404 | 채팅방 없음 |
| `CHATROOM_FORBIDDEN` | 403 | AI 토글 등 방장 전용 액션을 비방장이 시도 |
| `POLL_CURSOR_EXPIRED` | 410 | `after` 커서가 너무 오래되어 서버가 해당 지점 이후를 복원 못함 → 클라는 `/messages`로 히스토리 재로드 |

---

## 9. 구현 우선순위

백엔드 구현 순서 제안 (프론트 블로킹 순):

### Priority P0 (프론트 현재 mock — 실 API 대체 시급)
1. **`/diaries/feed`** — 홈 화면이 빈 mock으로 동작 중
2. **`/diaries/{id}`** — 상세 진입 불가능
3. **`/diaries/{id}/comments`** + `POST` — 댓글 기능
4. **`/diaries/{id}/like`** — 좋아요
5. **`/diaries`** POST — 일기 게시
6. **`/diaries/validate`** — AI 검증 (없으면 게시 자체 불가)

### Priority P1 (프론트 구현 전이지만 곧 필요)
7. **`/users/me`** 필드 확인 — 프로필 화면 작업 전 스펙 확정
8. **`/users/{id}`** — 타 사용자 프로필
9. **`/diaries/me`** — 내 일기 목록
10. **`/auth/refresh`** — 현재 이메일 로그인도 Bearer 방식인데 만료 대응 없음. 추가 시 장기 세션 가능
11. **`/auth/logout`** — 로그아웃 버튼 추후 추가 시

### Priority P2 (기능 확장)
12. `/comments/{id}/like`
13. `PUT /diaries/{id}`, `DELETE /diaries/{id}`
14. `DELETE /comments/{id}`
15. `/diaries/validate/line` (per-line 재검증)

### Priority P3 (Screen 3 음성 기반 텍스트 채팅방 — 설계 선행)
16. `/chatrooms/*` — 실시간 레이어는 **롱 폴링**, 오디오는 기존 `/chat/transcribe`+`/chat/speech` 재사용으로 확정 (2026-04-25). 남은 설계 포인트는 7.4 결정 필요 항목 (AI 응답 전략·참여자 상한·메시지 보존).

---

## 10. 검증 체크리스트 (API 완료 기준)

각 엔드포인트 개발 완료 후 아래를 체크:

- [ ] Happy path curl 예시 가능
- [ ] 인증 없이 접근 → 401 (보호된 엔드포인트)
- [ ] 잘못된 토큰 → 401
- [ ] 없는 리소스 접근 → 404
- [ ] 잘못된 body → 400 + 에러 코드 반환
- [ ] Response가 본 문서의 필드명/타입 정확히 일치 (camelCase 확인)
- [ ] ISO 8601 시간 포맷 확인
- [ ] CORS preflight 응답 정상 (`Access-Control-Allow-Origin`)
- [ ] Flutter 앱에서 `InjectionContainer`의 Mock을 Real impl로 교체 후 동작 확인

---

## 11. 참고 리소스

### 11.1 프론트엔드 Mock 구현 위치
백엔드 개발 중 어떤 shape을 기대하는지 확인용. mock을 실행시켜 응답을 관찰할 수 있음.

| 리소스 | 파일 |
|---|---|
| 일기 피드/상세/생성/좋아요 | `lib/src/data/repositories/diary_repository_mock_impl.dart` |
| 댓글 | `lib/src/data/repositories/diary_comment_repository_mock_impl.dart` |
| AI 검증 | `lib/src/data/repositories/diary_validation_repository_mock_impl.dart` |
| OAuth 교환/리프레시 | `lib/src/data/datasources/oauth_api_service.dart` + `lib/src/data/datasources/authenticated_http_client.dart` |

### 11.2 Flutter 모델 (파싱 정의)
| 모델 | 파일 |
|---|---|
| `Diary`, `DiaryAuthor`, `DiaryFeedResponse`, `DiaryLikeResponse` | `lib/src/data/models/diary/` |
| `DiaryComment`, `DiaryCommentListResponse` | `lib/src/data/models/diary/` |
| `DiaryValidation`, `DiaryLineValidation`, `DiaryLineStatus` | `lib/src/data/models/diary/diary_validation.dart` |
| `OAuthTokenResponse`, `OAuthProvider` | `lib/src/data/models/auth/` |
| `UserInfo` | `lib/src/data/models/user/user_info.dart` |

### 11.3 수동 테스트용 curl 모음
OAuth 이후 토큰을 환경변수 `$TOKEN`에 저장했다고 가정.

```bash
# 피드
curl -s "http://localhost:8080/api/v1/diaries/feed?size=5" \
  -H "Authorization: Bearer $TOKEN" | jq

# 상세
curl -s "http://localhost:8080/api/v1/diaries/1" \
  -H "Authorization: Bearer $TOKEN" | jq

# 게시
curl -s -X POST "http://localhost:8080/api/v1/diaries" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"lines":["a","b","c"],"tags":["#test"],"isPublic":true}' | jq

# 좋아요
curl -s -X POST "http://localhost:8080/api/v1/diaries/1/like" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"liked":true}' | jq

# AI 검증
curl -s -X POST "http://localhost:8080/api/v1/diaries/validate" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"lines":["오늘 일찍 일어났다.","커피 마셨다.","친구 만났어."]}' | jq

# 댓글
curl -s "http://localhost:8080/api/v1/diaries/1/comments?size=10" \
  -H "Authorization: Bearer $TOKEN" | jq

curl -s -X POST "http://localhost:8080/api/v1/diaries/1/comments" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"text":"좋아요!"}' | jq
```

---

## 12. 변경 이력
- 2026-04-24: v1 초안 작성 (프론트 구현 4개 화면 기준)

## 13. 문의
프론트엔드 측 질문/협의는 Flutter 레포지토리 `docs/prd/` 내 이슈 또는 대화 채널 통해 접수.
