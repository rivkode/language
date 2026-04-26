# Gemini Code Review Instructions

## General Principles
- Focus on backend API quality, not frontend UI
- Prioritize correctness over style
- Identify potential production issues

## Architecture Rules
- Controller must not contain business logic
- Use service/facade layer for orchestration
- Repository should only handle DB access

## Critical Review Points
- Race conditions and concurrency issues
- Transaction boundaries (@Transactional)
- N+1 query problems
- API idempotency
- Error handling and exception clarity

## Performance
- Avoid unnecessary DB queries
- Suggest caching if applicable (Redis, etc.)
- Check pagination strategy (offset vs cursor)

## Code Quality
- Avoid large methods (>50 lines)
- Ensure clear naming
- Remove dead code

## Testing
- If no tests exist → 반드시 지적
- Suggest unit test cases

## Output Style
- Be concise but actionable
- Provide code examples when suggesting improvements

