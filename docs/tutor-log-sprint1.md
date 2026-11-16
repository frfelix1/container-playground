# Tutor log: sprint 1

## Steps
| # | Step | Tier | Status |
|---|---|---|---|
| 1 | Close phase 1: typo, reformat, PR with 3-line explanation, merge | shallow | in progress |
| 2 | CRUD projects (list, create @Valid/@Size(max=300) -> 400, update, delete), then records | deep | |
| 3 | Dockerfile naive vs layered, MaxRAMPercentage, 1 break-it | medium | |
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

## Open items
| Item | Why deferred | Target |
|---|---|---|
| ~~Prove @Transactional test rollback~~ | Done in kata 2 | - |
| Tests run in [master].[dbo] | Needs init script | Sprint 2 |
