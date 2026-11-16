# Tutor log: sprint 1

## Steps
| # | Step | Tier | Status |
|---|---|---|---|
| 1 | Close phase 1: typo, reformat, PR with 3-line explanation, merge | shallow | done (PR #8) |
| 2 | CRUD projects (list, create @Valid/@Size(max=300) -> 400, update, delete), then records | deep | projects done (PR #9); records later |
| 3 | Dockerfile naive vs layered, MaxRAMPercentage, 1 break-it | medium | done |
| 4 | Compose: SQL Server + execution-api with healthchecks, 1 break-it | medium | |
| 5 | GitHub Actions: verify, build, Trivy, push GHCR by SHA | shallow | |
| 6 | README (diagram, quick start, badge), ADR 0001 (SQL Server amd64 under emulation) | shallow | |
| 7 | End of sprint: walkthrough, branch cleanup, drill, STAR story, notes-sprint1.md | - | |

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
- 3 lines: learner gave what/why in loose words, tutor shaped them; "could break" supplied from his lab. Pattern: learner skips the "could break" line.

## Open items
| Item | Why deferred | Target |
|---|---|---|
| ~~Prove @Transactional test rollback~~ | Done in kata 2 | - |
| Tests run in [master].[dbo] | Needs init script | Sprint 2 |
