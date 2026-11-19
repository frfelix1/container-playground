# Tutor log: sprint 1

## Where we stopped (2026-11-19, end of session) — READ FIRST
- Steps 1-4, 6, 7 done (PRs #8-#11, #13; notes on branch `sprint1/notes`). Tutor log lives on `sprint1/ci` (PR #12, draft) until CI merges.
- Step 5 BLOCKED: GitHub account locked for Actions (billing flag, $0 owed, support ticket open). When unlocked: re-run PR #12 (`./mvnw -B verify`), add build + Trivy + GHCR push, then README CI badge. Packages budget also shows "blocked": re-check.
- Next after unlock: finish step 5, then sprint 2. Carry: records CRUD, own DB + login instead of `master`, STAR story.
- Local `.env` exists with the example password (git-ignored).
- How this learner works best (keep doing):
  - ONE small step per message (one experiment, then wait for output). Big handouts overwhelm him (his request).
  - Work spans multiple days with context switching: open each session with a 3-line recap and a memory-jogger table.
  - He often skips predictions when tired. Don't push; ask for a one-word prediction max, and still explain the result.
  - Needs handholding on the 3 PR lines: give the "what we added" table + a fill-in sentence for "What", then tighten his draft.
  - Review mode: tutor writes code + tests; learner reads, predicts, runs, does one break-it, writes 3 lines.
  - Give the mental model BEFORE asking a prediction. Cold predictions fail and demotivate.
  - Medium tier: one batch per step (files + commands + experiments), check questions collected at the end.
  - Explain every flag; paste file paths + line numbers; tell him to `:checktime` after tutor edits (stale nvim buffers confused him once).
  - After each feature, a plain "what we added" table. He loses track otherwise, and he tends to skip the "could break it" line.
  - Motivation is fragile: keep steps short, aim for something visible each session.

## Steps
| # | Step | Tier | Status |
|---|---|---|---|
| 1 | Close phase 1: typo, reformat, PR with 3-line explanation, merge | shallow | done (PR #8) |
| 2 | CRUD projects (list, create @Valid/@Size(max=300) -> 400, update, delete), then records | deep | projects done (PR #9); records later |
| 3 | Dockerfile naive vs layered, MaxRAMPercentage, 1 break-it | medium | done |
| 4 | Compose: SQL Server + execution-api with healthchecks, 1 break-it | medium | done (PR #11) |
| 5 | GitHub Actions: verify, build, Trivy, push GHCR by SHA | shallow | BLOCKED: verify job written (PR #12 draft); account lock, support ticket open |
| 6 | README (diagram, quick start, badge), ADR 0001 (SQL Server amd64 under emulation) | shallow | done (PR #13), badge waits for CI |
| 7 | End of sprint: walkthrough, branch cleanup, drill, STAR story, notes-sprint1.md | - | done; STAR skipped |

## Session 2026-11-16
- MASTER_PLAN.md on main had leftover stash-conflict markers; resolved keeping the sprint version (PR #7, branch docs/fix-plan-conflict).
- phase1/projects-table: took main's fixed MASTER_PLAN.md (avoids merge conflict), reformatted tabs -> 4 spaces, fixed typo. 7 tests green.
- Rollback proof deferred to step 2 (learner didn't choose; will prove it in the CRUD tests).

## Session 2026-11-16 (cont.)
- Kata 2 (GET /projects) green, 9 tests, commit 423ff2b. Rollback PROVEN by containsExactlyInAnyOrder.
- Pacing changed on learner's request: deep tier batched (all failing tests for a feature at once, skeleton + context), max ~1 prediction per batch. Learner needs context given before predictions; asking cold failed.
- Predictions: partial. Named "different threads" for RANDOM_PORT; missed connection-level lock. Explained.
- Kata 3 started: added spring-boot-starter-validation, ProjectRequest record, 7 controller + 5 service failing tests.
- Learner unmotivated by fill-in-the-blanks ("won't write tests like this with AI"). Switched to REVIEW MODE (MASTER_PLAN updated): AI writes tests+code, learner reviews, writes 3 lines, does break-it. Then quick wins (steps 3-6) for something showable.
- Kata 3 implemented by tutor, 21 tests green. execution-api/pom.xml reformatted to spaces.
- Step 2 remaining: learner's @Valid break-it + 3 lines; records CRUD (tutor writes, learner reviews) can follow later.
- Review answers: verify/never correct; Optional.map partial ("loop of 0..1", missed why not if/get); DTO-hides-id wrong (pointed at no setId), spoiled on request.
- Learner didn't know why @Valid exists -> explained two gates (Jackson, Validator). Then predicted correctly: tests 90 + 101 break, 500. Twist seen: Errors not Failures, MockMvc rethrows NPE (no servlet error handling). Third "slice test != real server" example.
- Learner lost track of what the branch added; tutor gave plain table + draft lines. Learner rewrote them himself (good: break-it line in own words). "Why" was weak ("need for later"). PR #9.
- Lesson for tutor: give the mental model BEFORE any prediction; batch was still too much at once. Summarize "what we added" in a table after each feature.

## Step 3 plan (Dockerfile)
- Naive (single stage, JDK, fat jar) vs optimized (multi-stage, JRE, layered jar via `java -Djarmode=tools extract --layers`), non-root.
- Experiments: image size, rebuild time after a one-line Java change, `docker history`, MaxRAMPercentage with `--memory`.
- App needs SQL Server to start, so "run it for real" is step 4 (Compose). Step 3 proves the JVM in the container only.
- Built (tutor, 2026-11-16): naive 1.1GB, optimized 580MB, both ~18s cold. MaxHeapSize 384MB at --memory=512m (75%). uid 10001. Startup fails "Failed to determine a suitable driver class" (no datasource URL) = expected.
- Learner to do: 4 experiments (size, rebuild after 1-line change, docker history, 25% vs 75% heap) + break-it (remove the spring-boot-loader COPY).
- Results: size prediction "half" right, reason wrong (thought deps; really JDK + Maven + .m2). Confused by "9/9 vs 23/23" step counter -> explained. docker history needed full explanation. Heap: didn't know how to approach; given the 25%/75% facts, understood. Break-it: predicted loader layer correctly, saw build OK + run fails, exit 1, ls proof. Layer-chain cache invalidation seen in output.
- 3 lines: learner gave what/why in loose words, tutor shaped them; "could break" supplied from his lab. Pattern: learner skips the "could break" line. PR #10 merged.

## Step 4 (Compose)
- compose.yaml + .env.example on sprint1/compose. Tutor smoke test: both healthy in ~12s, POST 201, GET list OK, then `down -v`.
- Shortcuts (in compose.yaml): master db + sa login (sprint 2), API healthcheck = curl /projects (sprint 3 -> Actuator).
- Break-it planned: replace `condition: service_healthy` with plain `depends_on: [sqlserver]` and start from a fresh volume.
- Results (2026-11-18/19, run one step per message):
  - Exp 1: predicted sqlserver healthy first, correct. Proof: both created 21s ago, API "Up 14s" -> waited ~7s.
  - Exp 2: no predictions. All codes as expected (201, 200, 200, 204, 404, 400); explored more himself. Taught: IDENTITY ids never reused; real 400 vs MockMvc rethrow.
  - Exp 3: no predictions (tired), no output pasted; expected results explained (down keeps data, down -v empties).
  - Exp 4: MISCONCEPTION: thought SQL Server runs on the Mac. Output: 172.19.0.2 inside, "cannot resolve" on Mac. Explained with diagram: containers by service name, Mac via published ports, localhost inside container = itself.
  - Break-it: predicted crash, correct. Race DID trigger: API Exited (1) in ~2s, sqlserver healthy at 7s. Log: "TCP/IP connection to the host sqlserver, port 1433 has failed ... Connection refused" from flywayInitializer. Taught: find the plain sentence in Spring crash chains; DNS fine, nothing listening yet.
  - Check question on `$$` (line 26) skipped.
  - 3 lines: learner wrote all three, "could break" in own words (first time unprompted). "Why" said "no longer mocking" (wrong: tests already used real SQL Server); tutor reframed to "whole system as containers with one command, base for CI + README". PR #11.

## Open items
| Item | Why deferred | Target |
|---|---|---|
| ~~Prove @Transactional test rollback~~ | Done in kata 2 | - |
| Tests run in [master].[dbo] | Needs init script | Sprint 2 |

## Session 2026-11-19
- Recap given. DB check: tests use Testcontainers (TestcontainersConfiguration.java), so CI needs no DB service; runner Docker pulls mssql. No mvnw in repo -> CI uses runner's `mvn`.
- Wrote `.github/workflows/ci.yml` with verify job only (checkout, setup-java 21 temurin + maven cache, `mvn -B verify`). Commit a8cbb17, draft PR #12.
- Prediction "first run slower" correct; explained fresh VM + `cache: maven`.
- Runs blocked: "account is locked due to a billing issue". GitHub Free, $0 owed, no card, $0 budgets (keep them). Repo made PUBLIC (history checked: no secrets); still locked -> account flag. Support ticket opened. Learner declined `act`.
- When unlocked: re-run PR #12, then add build + Trivy + GHCR push (Packages budget also shows blocked, re-check). Add CI badge in README then.
- Correction: repo ROOT has mvnw + parent pom.xml (multi-module). ci.yml fixed to `./mvnw -B verify` from root (no working-directory). Untested until unlock.
- Open: 9 committed `*.tmp` files (8 under execution-api/src + pom.xml.tmp), likely reformat leftovers. Asked learner before deleting.
- Moved on to step 6 on branch `sprint1/docs` (badge skipped for now).
- Step 6 (PR #13): README quick start (`up --build -d --wait`), Mermaid diagram, ADR 0001 in docs/adr/. Deleted 9 empty committed *.tmp files, `*.tmp` in .gitignore. README not run end to end yet (learner will when expanding it).
- Predictions: `up -d` not ready immediately (correct -> added --wait); start_period "harmless" (correct; explained it adds zero wait).
- Walkthrough POST /projects: 201 + Location /projects/1. flyway_schema_history 3 rows correct (after down -v). Flow explain-back: skeleton right; missed service/repository split, DB-generated id, Compose DNS.
- Shortcut found: API uses `master` DB (compose.yaml:49). Target: sprint 2/3, own DB + login per service (MASTER_PLAN).
- Branch cleanup: 10 merged branches deleted local + remote. Kept main, sprint1/ci.
- Drill (8): strong Q3 depends_on healthy, Q6 ddl-auto validate. Partial Q1 multi-stage, Q2 @Valid (no status codes), Q5 MaxRAMPercentage (missed non-heap -> OOMKilled), Q7 .dockerignore. Gap Q4 Optional (2nd time, revisit), Q8 H2 vs Testcontainers.
- STAR skipped (learner: not useful now). Draft fill-in was the CI-lock diagnosis story; offer again once CI is green.
- notes-sprint1.md: learner too tired to write; picked from A/B/C/D options, tutor assembled on `sprint1/notes`. Works well when motivation is low.

## End of sprint 1 assessment
- Strong: Compose readiness, Flyway/Hibernate roles, predictions when given a model first.
- Partial: Docker internals vocabulary (multi-stage, build context, JVM memory), HTTP status reasoning.
- Gaps: Optional (repeat), test realism (H2 vs real DB).
- Carry forward: step 5 CI (blocked), records CRUD, own DB instead of master, STAR story.
