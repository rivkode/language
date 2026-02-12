# AI Assistant Role: Project Consultant (Ask Mode)

## 🎯 Instructions for Response
너는 이 프로젝트의 **분석가이자 기술 컨설턴트**로 동작한다. 사용자가 질문을 던질 때 아래 원칙을 반드시 준수해라:

1. **Ask Mode 전용:** 코드를 수정하거나 파일 변경을 위한 `diff`를 생성하지 마라. 대신 현재 코드의 구조, 로직, 흐름을 설명하는 데 집중해라.
2. **분석 위주:** 특정 기능이 어떻게 작동하는지, 아키텍처가 어떻게 구성되어 있는지 물을 때 프로젝트 내의 실제 파일 경로를 인용하며 상세히 설명해라.
3. **코드 예시:** 코드를 제안해야 할 경우, 파일을 수정하라는 명령이 아니라 학습이나 이해를 돕기 위한 **독립적인 코드 스니펫**으로만 제공해라.
4. **존댓말 사용:** 모든 답변은 한국어로 작성해라.

## Project Overview

This project is a Kotlin-based language learning application built with the Spring Boot framework. It appears to be a backend service that provides chat functionalities, including AI-powered chat generation, audio transcription, and speech synthesis. The application is designed with a layered architecture, separating concerns into `interfaces`, `application`, `domain`, and `infrastructure` layers. It leverages several modern technologies, including Spring AI for artificial intelligence features, Spring Data JPA for database interaction, Spring Security for authentication and authorization, Kafka for event-driven communication, and Redis for caching. The project is containerized using Docker.

## Building and Running

### Building the project

The project uses Gradle as a build tool. To build the project, run the following command in the root directory:

```bash
./gradlew build
```

### Running the project

The application can be run in several ways:

**1. Running with Gradle:**

```bash
./gradlew bootRun
```

**2. Running the JAR file:**

First, build the project to create an executable JAR file.

```bash
./gradlew bootJar
```

Then, run the JAR file:

```bash
java -jar build/libs/language-0.0.1-SNAPSHOT.jar
```

**3. Running with Docker:**

The project includes a `Dockerfile` for containerization. To run the application using Docker, you first need to build the Docker image:

```bash
docker build -t language-app .
```

Then, you can run the Docker container. Note that the application requires external services like a database, Redis, and Kafka. The `application-prod.yaml` file expects their configurations to be provided as environment variables.

A `docker-compose.yml` file is present but was not analyzed. It would likely provide a convenient way to run the application and its dependencies together.

## Development Conventions

*   **Language:** The project is written in Kotlin.
*   **Framework:** It uses the Spring Boot framework.
*   **Architecture:** The codebase is structured in a layered architecture, which is a good practice for maintainability and separation of concerns.
*   **Dependencies:** Key dependencies include Spring Web, Spring Data JPA, Spring Security, Spring AI, Kafka, and Redis.
*   **Testing:** The project uses JUnit 5 and Kotest for testing.
*   **API:** The application exposes a RESTful API. The API endpoints are defined in `...ApiController.kt` files within the `src/main/kotlin/com/learner/language/interfaces` directory.
*   **Configuration:** The application is configured using YAML files (`application.yml`, `application-prod.yaml`, etc.). Production configuration relies on environment variables for sensitive data.
*   **Containerization:** The project is set up to be built and run as a Docker container.

## Key Files

*   `build.gradle.kts`: The Gradle build file, which defines the project's dependencies and build process.
*   `src/main/kotlin/com/learner/language/LanguageApplication.kt`: The main entry point of the Spring Boot application.
*   `src/main/kotlin/com/learner/language/application/`: This package contains the application layer, which orchestrates the business logic.
*   `src/main/kotlin/com/learner/language/domain/`: This package contains the core business logic and domain models.
*   `src/main/kotlin/com/learner/language/infrastructure/`: This package contains the infrastructure layer, which deals with external concerns like databases, messaging queues, etc.
*   `src/main/kotlin/com/learner/language/interfaces/`: This package contains the presentation layer, including REST controllers that expose the application's functionality.
*   `src/main/resources/application-prod.yaml`: The production configuration file.
*   `Dockerfile`: Defines how to build the Docker image for the application.
