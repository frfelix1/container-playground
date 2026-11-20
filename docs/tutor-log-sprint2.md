# Tutor log, sprint 2

## Session 2026-11-20
- Kickoff given. Plan: steps 0-6 (0 CI leftover, 1 init script + logins, 2 iam schema, 3 Security/RBAC, 4 RestClient + token + records CRUD, 5 gui, 6 networks/README/GIF/ADR).
- Housekeeping: sprint1/ci and sprint1/notes deleted by learner.
- Learner wants ONE step at a time.
- Step 0 on `sprint2/ci-image`: `image` job (needs verify): build with load, Trivy CRITICAL/HIGH fixed-only (pinned SHA), push `ghcr.io/frfelix1/execution-api:<sha>` only on main.
- Push prediction: half right (GHCR yes), but credited branch protection instead of the `if:` main guard; corrected.
- Trivy failed: CVE-2026-97054 (Go stdlib) in /usr/bin/pebble. Learner found the target line himself; thought Trivy "stopped" at the finding (corrected: scans all, only failures print). Chose option A: `.trivyignore` with exp:2026-12-31.

## Open items
| Item | Why deferred | Target |
|---|---|---|
| execution-api on master/sa, tests in [master].[dbo] | Needs init script | Sprint 2 step 1 |
| Records CRUD | Sprint 1 carry | Sprint 2 step 4 |
| STAR story (CI lock diagnosis) | Skipped sprint 1 | End of sprint 2 |
| Revisit Optional, H2 vs Testcontainers | Drill gaps | Quirk drills sprint 2 |
| `.trivyignore` CVE-2026-97054 expires 2026-12-31 | Base image not rebuilt yet | Re-check before expiry |
| GHCR package is private by default | Not needed until kind pulls it | Sprint 3 |
