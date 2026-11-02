# Tutor log: phase 1

## Where we stopped (2026-11-02)
- Branch phase1/projects-table, NOT pushed. Suite: 6 tests green.
- Done this session: experiment 4 (@Table removed -> "missing table [project]"),
  spring.flyway.validate-migration-naming=true (committed, misnamed-file failure NOT run),
  break-it >300 chars -> regression test saveRejectsNameLongerThan300Chars (committed).
- Next step: pick from open items: end-to-end test (recommended) or @Transactional rollback proof.
- Quiet test command (fixed grep): ./mvnw -pl execution-api test 2>&1 | grep -E "Tests run:|\[ERROR\]|Expecting|Schema.validation"
- Learner context-switches a lot: start each session with a 3-line recap and paste code, don't rely on memory.

## Open items (carry forward)
| Item | Why deferred | Target |
|---|---|---|
| Tests run in [master].[dbo], not an app database | Deliberate shortcut | Phase 2 (init script creates appdata) |
| End-to-end test: real HTTP -> controller -> service -> repository -> DB | Controller test mocks the service; layers never checked together | Phase 1, after more CRUD |
| Limit 300 lives in V3 and in test (301); no Java validation | DB is last line of defence only | Phase 2: @Size(max=300) + shared constant, 400 instead of DB error |
| Mixed tabs/spaces; learner prefers spaces | Cosmetic | Any time: one reformat pass |
| @Transactional rollback in tests | Used but not proven | Phase 1, optional experiment |

## How the learner did
- Strong: Flyway mental model (fix forward, checksums need a long-lived DB, only V3 runs on existing DB), misnamed-file prediction, getters/Jackson key naming, wiring the service from the pattern.
- Partial: `final` (first guessed visibility), Spring Data proxy ("from Spring library").
- Wrong predictions (good learning moments): validate with an extra column (predicted fail; it passed), editing V1 on Testcontainers (predicted fail; it passed).
- Strong (2026-11-02): regression-test purpose (test forces a conscious change), choosing parent DataAccessException.
- Partial: predicting *which component* fails (said Hibernate; was SQL Server / Flyway). Thought @Table was needed regardless of DB name; now understands it decouples class name from table name.
- Note: misnamed-migration failure with validate-migration-naming was predicted, not run.

## Revisit with later context
- Phase 2 CI: a check that fails a PR modifying an existing file under db/migration/.
- Phase 2 deploy: `flyway validate` against a long-lived DB before deploy.
- NOT NULL column on a table with existing rows fails the migration; empty test DBs hide it.
- SQL Server image is amd64 only; runs emulated on Apple Silicon.
- Slice tests (@WebMvcTest) are blind to schema/migration/wiring problems; seen twice (exp 4, Flyway). Tie into test pyramid in phase 2 CI.
- Spring's DataAccessException translation: revisit when a second DB or error mapping shows up.
