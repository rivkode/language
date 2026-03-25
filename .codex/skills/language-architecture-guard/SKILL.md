---
name: language-architecture-guard
description: Use when changing or reviewing this Kotlin Spring Boot language backend to keep code aligned with the layered architecture, package placement, and dependency direction between interfaces, application, domain, infrastructure, and system.
---

# Language Architecture Guard

Use this skill when adding, moving, or reviewing backend code in this repository.

## Goal

Keep changes aligned with the existing layered architecture:

- `interfaces`: HTTP controllers and request/response DTOs
- `application`: facades that orchestrate use cases
- `domain`: business logic, entities, commands, infos, service interfaces
- `infrastructure`: repository and external system implementations
- `system`: framework configuration, security, exception plumbing

## Placement Rules

- Put new REST endpoints under `src/main/kotlin/com/learner/language/interfaces/...`
- Put use-case orchestration in `src/main/kotlin/com/learner/language/application/...`
- Put business rules in `src/main/kotlin/com/learner/language/domain/...`
- Put JPA, Redis, Kafka, email, and external client implementations in `src/main/kotlin/com/learner/language/infrastructure/...`
- Put Spring config, auth filters, and global exception handling in `src/main/kotlin/com/learner/language/system/...`

## Dependency Rules

- `interfaces` depends on `application`, not on `infrastructure`
- `application` coordinates `domain` services, but should not absorb domain rules
- `domain` defines abstractions and business behavior; avoid framework-heavy code here unless the project already does so
- `infrastructure` implements domain ports such as readers, writers, and repositories
- Avoid controller-to-repository shortcuts and cross-domain coupling unless the existing design already requires it

## Working Checklist

1. Find the nearest existing feature with the same shape before adding new files.
2. Keep naming consistent with the current codebase: `*ApiController`, `*Facade`, `*Service`, `*ServiceImpl`, `*Reader`, `*Writer`, `*Command`, `*Info`.
3. Reuse existing exception and response handling from `src/main/kotlin/com/learner/language/system/exception`.
4. If a change crosses layers, verify the dependency direction still points inward.
5. When reviewing, call out architecture drift before style issues.

## Repo Anchors

- Controllers: `src/main/kotlin/com/learner/language/interfaces`
- Facades: `src/main/kotlin/com/learner/language/application`
- Domain services: `src/main/kotlin/com/learner/language/domain`
- Implementations: `src/main/kotlin/com/learner/language/infrastructure`
- Framework setup: `src/main/kotlin/com/learner/language/system`
