# Tutor log: phase 1

## Where we stopped (2026-11-02, end of day)
- Branch phase1/projects-table, 7 tests green. Commit 2bed281 (end-to-end test) NOT pushed.
- Done: experiment 4, validate-migration-naming, >300-char regression test, end-to-end test over RANDOM_PORT.
- Quirk hit and explained: @Transactional test + RANDOM_PORT -> hang (uncommitted row locks Tomcat thread; cycle spans Java + DB so SQL Server's deadlock detector can't see it). Fix: no @Transactional, @AfterEach deleteAll().
- Next step: learner tightens the comment above ProjectEndToEndTest (says "services wait for each other"; real cause: uncommitted row locks the server thread). Then: optional rollback proof, reformat, then end-of-phase steps.
- Quiet test command: ./mvnw -pl execution-api test 2>&1 | grep -E "Tests run:|\[ERROR\]|Expecting|Schema.validation"
- Learner context-switches a lot: start each session with a 3-line recap and paste code, don't rely on memory.

## Open items (carry forward)
| Item | Why deferred | Target |
|---|---|---|
| Tests run in [master].[dbo], not an app database | Deliberate shortcut | Phase 2 (init script creates appdata) |
| Limit 300 lives in V3 and in test (301); no Java validation | DB is last line of defence only | Phase 2: @Size(max=300) + shared constant, 400 instead of DB error |
| @Transactional rollback in tests | Used but not proven | Sprint 1 step 2 (CRUD tests) |
| ~~Comment above ProjectEndToEndTest~~ | Done (learner rewrote it; typo fixed 2026-11-16) | – |
| ~~Mixed tabs/spaces~~ | Done 2026-11-16, 4 spaces | – |

Phase 1 closes inside sprint 1 (plan switched from phases to sprints). New log: docs/tutor-log-sprint1.md.
| RANDOM_PORT starts a 2nd Spring context (+ likely 2nd SQL Server container), ~7s | Harmless now; unverified | Phase 2 CI speed |

## How the learner did
- Strong: Flyway mental model (fix forward, checksums need a long-lived DB, only V3 runs on existing DB), misnamed-file prediction, getters/Jackson key naming, wiring the service from the pattern.
- Partial: `final` (first guessed visibility), Spring Data proxy ("from Spring library").
- Wrong predictions (good learning moments): validate with an extra column (predicted fail; it passed), editing V1 on Testcontainers (predicted fail; it passed).
- Strong (2026-11-02): regression-test purpose (test forces a conscious change), choosing parent DataAccessException.
- Partial: predicting *which component* fails (said Hibernate; was SQL Server / Flyway). Thought @Table was needed regardless of DB name; now understands it decouples class name from table name.
- Note: misnamed-migration failure with validate-migration-naming was predicted, not run.
- Partial (2026-11-02 pm): predicted server sees uncommitted row (first instinct "can't see inside the transaction" was right, then talked himself out of it). Suggested "async" as fix. Got "commit it" with a hint. Thread dumps (jstack) too advanced for now; answer given.
- Strong: understood deadlock framing and why the regression test should break if the limit changes.

## Revisit with later context
- Phase 2 CI: a check that fails a PR modifying an existing file under db/migration/.
- Phase 2 deploy: `flyway validate` against a long-lived DB before deploy.
- NOT NULL column on a table with existing rows fails the migration; empty test DBs hide it.
- SQL Server image is amd64 only; runs emulated on Apple Silicon.
- Slice tests (@WebMvcTest) are blind to schema/migration/wiring problems; seen twice (exp 4, Flyway). Tie into test pyramid in phase 2 CI.
- Spring's DataAccessException translation: revisit when a second DB or error mapping shows up.
