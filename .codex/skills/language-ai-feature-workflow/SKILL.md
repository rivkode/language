---
name: language-ai-feature-workflow
description: Use when implementing or updating AI-powered features in this repository, including chat, prompt templates, OpenAI integration, audio transcription, speech synthesis, and prompt-driven feedback flows.
---

# Language AI Feature Workflow

Use this skill when a task touches chat generation, prompt templates, OpenAI configuration, audio transcription, speech synthesis, or feedback generation.

## Goal

Change AI features without scattering prompt logic or breaking request flow.

## First Read

- `src/main/kotlin/com/learner/language/domain/ai`
- `src/main/kotlin/com/learner/language/system/config/AiChatConfig.kt`
- `src/main/kotlin/com/learner/language/system/config/OpenAiEmbeddingConfig.kt`
- `src/main/resources/prompts`

## Workflow

1. Identify the user-facing entrypoint in `interfaces`.
2. Trace the facade and domain service that owns the AI use case.
3. Locate the prompt template or model configuration already used by that flow.
4. Change prompt files and Kotlin orchestration together; do not update one without the other.
5. Verify fallback behavior, error handling, and model-specific assumptions.

## Prompt Rules

- Keep prompt text in `src/main/resources/prompts` unless the change is trivial and truly code-local
- Prefer editing an existing prompt family before inventing a new naming pattern
- If a prompt is model-specific, make that explicit in the filename as this repo already does
- When changing prompt variables, verify the Kotlin side still supplies the full variable set

## Integration Rules

- Keep vendor and SDK details near config or infrastructure boundaries
- Avoid embedding large prompts directly in service classes
- Make AI outputs pass through domain-friendly command/info objects where possible
- If the feature is async or evented, inspect Kafka flow before assuming request-response behavior

## Verify

- Run the narrowest relevant tests first
- If no tests cover the AI path, add at least one service-level or controller-level guardrail test where practical
- Manually inspect prompt filenames and config bindings for mismatches

## Repo Anchors

- AI domain: `src/main/kotlin/com/learner/language/domain/ai`
- Audio infra: `src/main/kotlin/com/learner/language/infrastructure/audio`
- AI config: `src/main/kotlin/com/learner/language/system/config`
- Prompt templates: `src/main/resources/prompts`
