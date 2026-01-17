# xiuxian Development Guidelines

Auto-generated from all feature plans. Last updated: 2026-01-13

## Active Technologies

- Java 17+ + Spring Boot 3.x, MyBatis-Plus 3.x, MySQL 8.x, Lombok, SLF4J+Logback (001-xiuxian-docs)

## Project Structure

```text
backend/
frontend/
tests/
```

## Commands

# Add commands for Java 17+

## Code Style

Java 17+: Follow standard conventions

## Recent Changes

- 001-xiuxian-docs: Added Java 17+ + Spring Boot 3.x, MyBatis-Plus 3.x, MySQL 8.x, Lombok, SLF4J+Logback

<!-- MANUAL ADDITIONS START -->
<!-- MANUAL ADDITIONS END -->
# Custom Skills
- **Save Prompt**:
  - Description: Save a prompt or note to the project's prompt log.
  - Command: echo "[$(date)] $1" >> prompts.md && echo "Prompt saved."
  - Usage: type "save 'your prompt here'"