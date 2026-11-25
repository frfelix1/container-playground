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
- PR #16 merged. Step 2 on `sprint2/iam-schema`: new module `provisioning-api` (webmvc + jdbc/JdbcClient + Flyway, no JPA yet), IamSchemaTest written first; red for the right reason (Invalid object name 'users'/'roles').
- Closed "tests in [master]" (learner OK). Gate round 1: Q1 partial (users, roles only; missed permissions + 2 join tables), Q2 didn't know, Q3 close (said roles, it's rows/users).
- Round 2: Q1 correct (all 5). Q2 half: had containsExactly = order matters, missed ORDER BY p.name = alphabetical rows; tutor completed it.
- Green: V1__create_iam_schema.sql + V2__seed_roles_and_users.sql, 7/7 pass. Shortcut logged: demo users/passwords in a migration (not dev-only).
- Gate round 3: Q1, Q3 correct. Q2 gave the HASH as the password, then "htpasswd" -> told: editor-pass (comment line 16). Follow-up on why hash-in-Git is OK but plaintext comment isn't: good.
- Added provisioning-api/Dockerfile + Compose service (8081, iam/iam_svc); both Dockerfiles now COPY both module poms (reactor needs every listed module).
- Tutor mistake: ran a hidden-output smoke test that hung ~30 min (port clash, then init stuck "Created"; Docker couldn't stop it). Learner rightly called it out. New rules: time limit on every command, never hide output, ask before heavy runs. Leftover smoke containers handed to learner (docker rm -f).
- Learner chose B: Compose check for provisioning-api deferred to step 3 (needs a healthcheck endpoint).
- Session 2026-11-24: learner skipped the break-it predict/run after a few days away; tutor gave the answer (UPPER removal: nothing fails, SQL Server default collation is case-insensitive). Not run. Revisit collation in a step 3 drill (username lookup at login).
- Step 3 on `sprint2/security-rbac` (uncommitted): security starters, SecurityRulesTest first (401/403 + RBAC table; allowed = 404 until step 4). Design: HTTP Basic + STATELESS, CSRF off in the API (gui keeps it on), so no /login page: healthcheck endpoint to be chosen in the Compose action.
- Gate: Q1 right after a nudge (editor lacks DELETE), Q2 right, Q3 right (plain password, base64; tutor added "Spring hashes and compares").
- Red run prediction wrong: said 500 for missing /api/me (security runs before controllers; missing = 404). Result: GETs 401, all writes 403; 5 tests passed for the wrong reason. Learner named CSRF himself but didn't know it; explained (cookie forgery, GET is safe).
- Green code written, not yet run: SecurityConfig (JdbcDaoImpl on iam tables, permission rules, denyAll, /error permitAll), MeController. Check questions pending.
- Gate (2026-11-25): Q1 half (permission, said "the JOIN"; it's the SELECT p.name). Asked POST/PUT/PATCH, explained. Q2 wrong then vague ("an error") -> tutor completed: line 53 denyAll, 403. Q3 first "fail fast" (wrong), then close (forward hits denyAll); corrected framing: it's about the true status being hidden, not speed.
- Green run: 10/11 (no prediction given). /api/me also listed FACTOR_PASSWORD (Spring Security 7 MFA factor authority). Learner: not in DB (right), "factor = hash factor" (wrong -> MFA factor), chose fix A (filter in MeController, right, reason partly "don't change tests").
- Fix: filter FactorGrantedAuthority by type. Predicted all pass: correct, 11/11 green.
- Break-it (httpBasic commented out): prediction vague ("401 or 403?"), thought credentials no longer sent (corrected: sent, not read). Diagnosis from failure lines all correct: all 403, the 3 passing expect 403 (wrong-reason trap recognised), noCredentials should be 401.
- Healthcheck: learner chose A (Actuator /actuator/health, permitAll GET; Compose curl -fs). Replaces the planned /login. healthIsOpenWithoutLogin test added; compiles, not yet run.
- Gate: /actuator/env anonymous -> denyAll -> 401: correct incl. reasoning. 12/12 green.
- First Compose run of provisioning-api: up --build --wait, all healthy in 23s. Prediction fully correct (init Exited 0). Open item "never run in Compose" CLOSED.
- curl round: predicted 200/403/403, got 401/404/403. Missed that #1 had no -u, and that /** matches zero segments. The 404 JSON body proved the /error permitAll line works on a real server.
- Collation drill (curl -u Admin:admin-pass): predicted 200 (right) and username "ADMIN" via UPPER (wrong: UPPER only ran once in V2 seed). Got 200 + "admin". Tutor's own guess ("Admin", as typed) was also wrong: JdbcDaoImpl returns the username from the DB row.
- Learner chose A: keep case-insensitive usernames, pinned by test usernameIsCaseInsensitiveAndNormalisedFromTheDatabase.

## Open items
| Item | Why deferred | Target |
|---|---|---|
| Records CRUD | Sprint 1 carry | Sprint 2 step 4 |
| STAR story (CI lock diagnosis) | Skipped sprint 1 | End of sprint 2 |
| Revisit Optional, H2 vs Testcontainers | Drill gaps | Quirk drills sprint 2 |
| `.trivyignore` CVE-2026-97054 expires 2026-12-31 | Base image not rebuilt yet | Re-check before expiry |
| GHCR package is private by default | Not needed until kind pulls it | Sprint 3 |
| Demo users + known passwords seeded by a Flyway migration (every env) | Fine for local demo | Before anything faces real users (dev-only seed) |
| provisioning-api in Compose never run (image build + startup unverified) | Smoke test hung; no healthcheck yet | DONE 2026-11-25 (healthy via /actuator/health) |
