# Language Learning 애플리케이션 (jamoai.app)

## 프로젝트 개요

이 프로젝트는 한국어 학습을 위한 백엔드 서비스로, 사용자들이 한국어를 효과적으로 학습할 수 있도록 다양한 AI 기반 기능을 제공합니다. 주요 기능으로는 AI 기반 채팅 생성, 음성 오디오 전사(transcription), 그리고 음성 합성(speech synthesis), 문장 피드백 등이 있습니다. 현재 https://jamoai.app 에서 웹 서비스로 배포되어 사용 가능합니다.

이 애플리케이션은 계층형 아키텍처(interfaces, application, domain, infrastructure)를 사용하여 각 기능이 명확하게 분리되어 있으며, 유지보수 및 확장이 용이하도록 설계되었습니다.

## 기술 스택

이 프로젝트는 다음과 같은 주요 기술 스택을 활용합니다:

*   **언어:** Kotlin
*   **프레임워크:** Spring Boot
*   **AI 통합:** Spring AI (OpenAI API 연동)
*   **데이터베이스:** MySQL 8.0 (JPA를 통한 데이터 영속성 관리)
*   **캐시:** Redis 7
*   **메시징 큐:** Kafka 7.5 (이벤트 기반 통신)
*   **인증/권한:** Spring Security, JWT (JSON Web Tokens)
*   **API 문서화:** Swagger
*   **컨테이너화:** Docker
*   **테스팅:** JUnit 5, Kotest

## 프로젝트 구조

프로젝트는 다음의 계층형 아키텍처로 구성되어 있습니다:

*   **`interfaces/`**:
    *   **역할:** 애플리케이션의 외부 인터페이스(주로 REST API)를 정의합니다. 클라이언트 요청을 받아 `application` 계층으로 전달하고, 결과를 클라이언트에 반환하는 역할을 합니다.
    *   **예시:** `ChatApiController.kt`와 같은 컨트롤러들이 이 계층에 속하여 HTTP 요청을 처리합니다.
*   **`application/`**:
    *   **역할:** 비즈니스 로직을 조정하고 도메인 계층의 서비스를 활용하여 유스케이스를 구현합니다. `interfaces` 계층과 `domain` 계층 사이의 중개자 역할을 합니다.
    *   **예시:** `ChatFacade.kt`는 클라이언트 요청에 따라 `ChatService`를 호출하여 채팅 관련 비즈니스 로직을 수행합니다.
*   **`domain/`**:
    *   **역할:** 핵심 비즈니스 로직과 도메인 모델을 포함합니다. 애플리케이션의 핵심 규칙과 데이터를 정의하며, 인프라 계층에 의존하지 않습니다.
    *   **예시:** `ChatService.kt` 인터페이스 및 `ChatServiceImpl.kt` 구현체가 이 계층에 속하며, 실제 채팅 메시지 저장, 생성 등의 로직을 담당합니다.
*   **`infrastructure/`**:
    *   **역할:** 외부 시스템(데이터베이스)과의 연동을 담당합니다. `domain` 계층의 인터페이스를 구현하며, 기술적인 세부 사항을 캡슐화합니다.
    *   **예시:** `ChatRepository.kt`와 같은 데이터 저장소 구현체들이 이 계층에 속합니다.

## 빌드 및 실행

### 프로젝트 빌드하기

이 프로젝트는 Gradle을 빌드 도구로 사용합니다. 프로젝트를 빌드하려면 루트 디렉토리에서 다음 명령어를 실행하세요:

```bash
./gradlew build
```

### 프로젝트 실행하기

애플리케이션은 다음 방법으로 실행할 수 있습니다:

**1. Gradle로 실행 (개발용)**

```bash
./gradlew bootRun
```

**2. JAR 파일로 실행**

먼저, 실행 가능한 JAR 파일을 생성하기 위해 프로젝트를 빌드합니다:

```bash
./gradlew bootJar
```

그 다음, 생성된 JAR 파일을 실행합니다:

```bash
java -jar build/libs/language-0.0.1-SNAPSHOT.jar
```

**3. Docker로 실행**

프로젝트는 컨테이너화를 위한 `Dockerfile`을 포함하고 있습니다. Docker를 사용하여 애플리케이션을 실행하려면, 먼저 Docker 이미지를 빌드해야 합니다:

```bash
docker build -t language-app .
```

이미지 빌드 후, Docker 컨테이너를 실행할 수 있습니다. 애플리케이션은 데이터베이스, Redis, Kafka와 같은 외부 서비스가 필요합니다. `application-prod.yaml` 파일은 이러한 서비스의 설정을 환경 변수로 제공하도록 기대합니다.

`docker-compose.yml` 파일도 존재하지만, 본 README 작성 시 분석에 포함되지 않았습니다. 이 파일은 애플리케이션과 해당 의존성을 함께 실행하는 편리한 방법을 제공할 수 있습니다.

## 개발 컨벤션

*   **언어:** Kotlin을 주력으로 사용합니다.
*   **프레임워크:** Spring Boot의 컨벤션을 따릅니다.
*   **아키텍처:** `interfaces`, `application`, `domain`, `infrastructure`로 구성된 계층형 아키텍처를 따릅니다.
*   **테스팅:** JUnit 5 및 Kotest를 사용하여 테스트 코드를 작성합니다.
*   **API:** RESTful API를 제공하며, API 엔드포인트는 `src/main/kotlin/com/learner/language/interfaces` 디렉토리 내의 `...ApiController.kt` 파일에 정의됩니다.
*   **설정:** YAML 파일을 통해 애플리케이션을 설정합니다 (`application.yml`, `application-prod.yaml` 등). 운영 환경 설정은 환경 변수를 활용하여 민감한 데이터를 관리합니다.
*   **컨테이너화:** Docker를 통한 컨테이너 기반 배포를 지원합니다.
