# Tutor log: phase 1

## Open items (carry forward)
| Item | Why deferred | Target |
|---|---|---|
| Tests run in [master].[dbo], not an app database | Deliberate shortcut | Phase 2 (init script creates appdata) |
| End-to-end test: real HTTP -> controller -> service -> repository -> DB | Controller test mocks the service; layers never checked together | Phase 1, after more CRUD |
| Experiment 4: delete @Table, verify Hibernate looks for "project" | Comment in Project.java is unverified | Phase 1 (next step) |
| spring.flyway.validate-migration-naming=true | Misnamed migrations are silently ignored | Phase 1, after experiment 4 |
| `name` is NVARCHAR(300) (V3) but Java String is unlimited; validate ignores length | Breakable on purpose: insert >300 chars | Phase 1 break-it, or validation in phase 2 |
| Mixed tabs/spaces; learner prefers spaces | Cosmetic | Any time: one reformat pass |
| `final` on service dependencies | Mentioned; check ProjectController already does it | Phase 1 |
| @Transactional rollback in tests | Used but not proven | Phase 1, optional experiment |

## How the learner did
- Strong: Flyway mental model (fix forward, checksums need a long-lived DB, only V3 runs on existing DB), misnamed-file prediction, getters/Jackson key naming, wiring the service from the pattern.
- Partial: `final` (first guessed visibility), Spring Data proxy ("from Spring library").
- Wrong predictions (good learning moments): validate with an extra column (predicted fail; it passed), editing V1 on Testcontainers (predicted fail; it passed).

## Revisit with later context
- Phase 2 CI: a check that fails a PR modifying an existing file under db/migration/.
- Phase 2 deploy: `flyway validate` against a long-lived DB before deploy.
- NOT NULL column on a table with existing rows fails the migration; empty test DBs hide it.
- SQL Server image is amd64 only; runs emulated on Apple Silicon.
