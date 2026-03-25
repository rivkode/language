---
name: language-api-and-domain-delivery
description: Use when adding a new backend capability to this repository by threading it cleanly through controller, DTO, facade, domain service, and infrastructure layers with the existing naming and packaging conventions.
---

# Language API And Domain Delivery

Use this skill when shipping a new feature or endpoint in this repository.

## Goal

Implement end-to-end backend changes in the existing house style without skipping layers.

## Delivery Path

1. Add or extend request/response DTOs in `interfaces`.
2. Add or extend the controller entrypoint in `interfaces`.
3. Route the use case through the relevant facade in `application`.
4. Express inputs and outputs with `*Command` and `*Info` types in `domain` where appropriate.
5. Implement or extend domain services.
6. Add infrastructure adapters if persistence or external integration is required.
7. Cover the change with the narrowest effective tests.

## Design Rules

- Keep controllers thin; mapping and delegation belong there, not business rules
- Keep facades focused on orchestration across services
- Put invariants and decisions in domain services or domain models
- Prefer extending an existing feature package before creating a brand-new top-level package
- Follow the repo's DTO and naming patterns before introducing new abstractions

## Common Feature Anchors

- Chat flow: `src/main/kotlin/com/learner/language/interfaces/chat` and `src/main/kotlin/com/learner/language/domain/chat`
- Feedback flow: `src/main/kotlin/com/learner/language/interfaces/feedback` and `src/main/kotlin/com/learner/language/domain/feedback`
- Sentence flow: `src/main/kotlin/com/learner/language/interfaces/sentence` and `src/main/kotlin/com/learner/language/domain/sentence`
- Word flow: `src/main/kotlin/com/learner/language/interfaces/word` and `src/main/kotlin/com/learner/language/domain/word`
- User flow: `src/main/kotlin/com/learner/language/interfaces/user` and `src/main/kotlin/com/learner/language/domain/user`

## Done Criteria

- The feature is reachable from the intended API entrypoint
- Layer boundaries remain intact
- Names and package placement match nearby code
- Relevant tests or verification steps are completed
