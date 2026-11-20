# Tutor log, sprint 2

## Session 2026-11-20
- Kickoff given. Plan: steps 0-6 (0 CI leftover, 1 init script + logins, 2 iam schema, 3 Security/RBAC, 4 RestClient + token + records CRUD, 5 gui, 6 networks/README/GIF/ADR).
- Housekeeping: sprint1/ci and sprint1/notes deleted by learner.
- Learner wants ONE step at a time.
- Step 0 on `sprint2/ci-image`: `image` job (needs verify): build with load, Trivy CRITICAL/HIGH fixed-only (pinned SHA), push `ghcr.io/frfelix1/execution-api:<sha>` only on main.
- Push prediction: half right (GHCR yes), but credited branch protection instead of the `if:` main guard; corrected.
- Trivy failed: CVE-2026-97054 (Go stdlib) in /usr/bin/pebble. Learner found the target line himself; thought Trivy "stopped" at the finding (corrected: scans all, only failures print). Chose option A: `.trivyignore` with exp:2026-12-31.
- Trivy prediction: "mentions it but skips" -> corrected: ignored findings are silent unless --show-suppressed.
- 3 lines written by tutor on learner's request (shallow tier, allowed).
- Learner declined the Node 20 bump (checkout/setup-java v4 stay). Step 0 done; first GHCR push hit "denied" on the manifest, a re-run with no change fixed it (likely a first-push race; my "missing access" guess was wrong).
- Step 1 on `sprint2/db-logins`: db/init.sql (idempotent, sqlcmd vars), `sqlserver-init` one-shot service, execution-api -> appdata/appdata_svc via service_completed_successfully. Tutor smoke test in isolated project `smoke`: up --wait OK, init Exited 0, Flyway tables in appdata, appdata_svc -d iam = "Cannot open database", re-run idempotent.
- Proposed closing "tests in [master]": the Testcontainers DB is throwaway, sa there exposes nothing. Learner to confirm.
- Gate: Q1 "user in iam" right (said table, corrected to database). Q2 wrong twice: thought one command runs on the Mac -> SAME misconception as sprint 1 Exp 4 (localhost = the container itself). Explained with diagram; revisit in step 6 DNS lab. Q3 partial (missed "job exits, never healthy").
- Exp 1 prediction wrong: thought down -v / grep removed passwords (mixed up volume vs .env). Read the ps output with help: Exited (0) = success for a job.
- Exp 2 predictions both correct (appdata OK, iam refused). Taught: first error line is the cause.
- Break-it (-S localhost): prediction vague ("sqlserver can't initialize"); actually sqlserver healthy, init Exited (1) with TCP 0x2749 / Login timeout, execution-api stuck in "Created" (never started). Didn't write own diagnosis sentence.
- PR #16 lines: learner picked from A/B options (A, B, A). First CI image run failed: wget couldn't fetch the Maven 3.10.0 distribution from repo.maven.apache.org inside the Docker build (transient); re-run passed.

## Open items
| Item | Why deferred | Target |
|---|---|---|
| execution-api on master/sa, tests in [master].[dbo] | Needs init script | Sprint 2 step 1 |
| Records CRUD | Sprint 1 carry | Sprint 2 step 4 |
| STAR story (CI lock diagnosis) | Skipped sprint 1 | End of sprint 2 |
| Revisit Optional, H2 vs Testcontainers | Drill gaps | Quirk drills sprint 2 |
| `.trivyignore` CVE-2026-97054 expires 2026-12-31 | Base image not rebuilt yet | Re-check before expiry |
| GHCR package is private by default | Not needed until kind pulls it | Sprint 3 |
