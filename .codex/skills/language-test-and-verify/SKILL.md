---
name: language-test-and-verify
description: Use when validating changes in this repository so new features and refactors are covered by the right mix of unit tests, integration tests, and targeted Gradle verification commands.
---

# Language Test And Verify

Use this skill after changing Kotlin code in this repository, especially for service logic, repository wiring, controller behavior, or AI orchestration.

## Goal

Match the verification level to the risk of the change instead of defaulting to either no tests or full-suite runs.

## Test Map

- Unit-style service tests: `src/test/kotlin/com/learner/language/service`
- Integration tests: `src/test/kotlin/com/learner/language/integration`
- Shared test setup: `src/test/kotlin/com/learner/language/testutils`

## Selection Rules

- Business rule changes: start with service tests
- Persistence or wiring changes: add or update integration tests
- API contract changes: verify controller behavior indirectly through facade/service coverage or add API-focused tests if needed
- Cross-cutting config changes: run the narrow test plus at least one broader smoke check

## Execution Pattern

1. Run the smallest relevant Gradle test target first.
2. Expand scope only if the change crosses multiple modules or infrastructure boundaries.
3. If a failing test reveals unclear behavior, fix the behavior or encode the intended contract before widening coverage.

## Writing Rules

- Reuse fixtures from `src/test/kotlin/com/learner/language/testutils/fixture`
- Reuse persistence helpers from `src/test/kotlin/com/learner/language/testutils/persistence`
- Keep tests aligned with current naming patterns such as `*ServiceTest` and `*IntegrationTest`
- Prefer one behavior per test over broad scenario bundles

## Verify Checklist

- New or changed branch behavior is covered
- Exceptions and validation failures are covered when relevant
- Repository or config changes do not rely only on compilation
- Final response mentions what was tested and what was not tested
