# Repository Guidelines

## Project Structure & Module Organization
This Spring Boot service keeps executable code in `src/main/java/com/club/campusclubmanager`, currently centered on `CampusClubManagerBackendApplication.java`. Feature packages (e.g., `event`, `member`) should live under the same namespace to keep wiring predictable. Shared resources (templates, static assets, and configuration) reside in `src/main/resources`, with `application.yaml` as the primary profile entry point; keep environment-specific overrides in sibling `application-<profile>.yaml` files. Tests mirror the main tree inside `src/test/java/com/club/campusclubmanager` to keep package-private coverage straightforward.

## Build, Test, and Development Commands
- `./mvnw clean package` (`mvnw.cmd` on Windows) compiles with Java 21, runs all tests, and produces the runnable jar in `target/`.
- `./mvnw spring-boot:run -Dspring-boot.run.profiles=local` starts the API with hot reload of classpath resources; adjust the profile to match your datasource settings.
- `./mvnw test -Dtest=CampusClubManagerBackendApplicationTests` executes a focused suite when iterating on a single specification.

## Coding Style & Naming Conventions
Use 4 spaces, no tabs, and follow Spring conventions: classes are `PascalCase`, methods and fields `camelCase`, constants `UPPER_SNAKE_CASE`. Keep package names lowercase and singular (`.controller`, `.service`, `.repository`). Lombok is enabled; prefer `@Getter`/`@Setter` over handwritten boilerplate but document any generated behavior. Validate APIs with Java records or DTOs under `.dto` to keep request models isolated.

## Testing Guidelines
`spring-boot-starter-test` supplies JUnit 5 and AssertJ; new test classes belong under the same package path as the code they verify and should end with `Tests`. Aim for meaningful integration coverage around REST controllers and repositories; stub out external systems with `@MockBean`. Run `./mvnw test` locally before pushing and include evidence of any new failing scenarios in the PR discussion.

## Commit & Pull Request Guidelines
History currently uses gitmoji-prefixed Conventional Commit messages (e.g., `:sparkles:feat: add event scheduling api`). Keep subject lines imperative and under 72 characters, and describe motivation plus impact in the body. Pull requests must state the problem, explain the solution, list manual test steps, and reference tracking issues. Screenshots or JSON samples help reviewers validate contract changes, and requests touching data stores should call out migration or seed requirements.

## Security & Configuration Tips
Never commit secrets to `application.yaml`; instead rely on environment variables or `application-<profile>.yaml` listed in `.gitignore`. When adding datasource settings, require SSL where possible and document JDBC URLs in the PR. Use role-based method security when exposing new controllers, and ensure CORS or CSRF rules stay intentional.
