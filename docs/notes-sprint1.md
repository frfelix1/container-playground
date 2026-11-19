# Sprint 1 notes

## What I built
- CRUD API for projects (Spring Boot + SQL Server, Flyway migrations), 21 tests on real SQL Server via Testcontainers.
- Multi-stage Dockerfile (JDK builds, JRE runs) and Compose with healthchecks, so the API waits for SQL Server to be ready.
- README with quick start and a Mermaid diagram, plus ADR 0001 (why SQL Server runs emulated on my Mac).

## How a request flows
- curl -> port map -> controller (`@Valid`) -> service -> Spring-generated repository -> Hibernate INSERT
  -> SQL Server creates the id -> 201 + Location.

## Things I got wrong (and the right answer)
- Optional: `findById` returns a box with a project or nothing. `map` -> 200, `orElseGet` -> 404.
  Better than null: the type forces me to handle "missing".
- H2 vs Testcontainers: H2 only imitates SQL Server, so syntax and behavior (case-insensitive text) can differ.
  Testcontainers runs the real thing.
- Without `@Valid`, `""` is saved (201) and a too-long name gives a 500. Validation turns both into a 400.
- `.dockerignore` decides what the builder gets. It keeps `.env` out of image layers.

## What's still open
- CI: verify job green on main (~1 min). Still to add: image build, Trivy scan, GHCR push by SHA.
