# Tutor log, sprint 2

## Session 2026-11-20
- Kickoff given. Plan: steps 0-6 (0 CI leftover, 1 init script + logins, 2 iam schema, 3 Security/RBAC, 4 RestClient + token + records CRUD, 5 gui, 6 networks/README/GIF/ADR).
- Housekeeping: sprint1/ci and sprint1/notes deleted by learner.
- Learner wants ONE step at a time.
- Step 0 on `sprint2/ci-image`: `image` job (needs verify): build with load, Trivy CRITICAL/HIGH fixed-only (pinned SHA), push `ghcr.io/frfelix1/execution-api:<sha>` only on main.

## Open items
| Item | Why deferred | Target |
|---|---|---|
| execution-api on master/sa, tests in [master].[dbo] | Needs init script | Sprint 2 step 1 |
| Records CRUD | Sprint 1 carry | Sprint 2 step 4 |
| STAR story (CI lock diagnosis) | Skipped sprint 1 | End of sprint 2 |
| Revisit Optional, H2 vs Testcontainers | Drill gaps | Quirk drills sprint 2 |
| GHCR package is private by default | Not needed until kind pulls it | Sprint 3 |
