# Context Handoff: Java + Docker & Kubernetes Learning Project

## Who I am and the goal

- Strong DevOps background: CI/CD, Linux, cloud, Azure DevOps at expert level.
- Little practical Docker/Kubernetes experience. University-level Java from a few years ago; not comfortable writing it from scratch.
- **Target role: Systemutveckling Java.** The role asks for:
  - Java at a stated competence level.
  - MVC architecture and Maven (or similar).
  - Deep web development: AJAX, REST, WebServices (SOAP in practice).
  - Databases, primarily **SQL Server**.
  - Eclipse or equivalent IDE, and test-driven development.
  - Documented Java training and the ability to teach Java.
- Primary goal: practise every item above in one realistic system, built with an architect's mindset.
- Secondary goal: build real Docker/Kubernetes understanding for the CV and interviews, run like a real team would (CI/CD, GitOps).
- AI-driven or spec-driven development is not a learning goal. AI generates code; I learn by reading, changing and breaking it.

## Learning approach

There are three tracks, each with its own style.

### Infrastructure (Dockerfiles, YAML, Helm, CI)

- Generate working, heavily commented boilerplate. Do not make me write it from scratch.
- Teach through `change -> predict -> run -> explain`. Ask me to predict and wait for my answer before revealing it.
- Prefer side-by-side comparisons, such as a naive and optimized Dockerfile.
- Break-it labs: tell me how to break it, not what the symptom will be. I diagnose with `docker logs/inspect`, `kubectl describe/logs` and events before you explain.

### Java and Spring

- Generate the skeleton and the first working feature of each module, heavily commented with *why*, not only *what*.
- Then give me exercises of three kinds:
  1. **TDD katas.** You write a failing test (for example "an EDITOR cannot delete a project"). I make it pass. Red -> green -> refactor.
  2. **Extend.** I add a feature by following the pattern of an existing one (a new endpoint, entity, migration or view).
  3. **Break-it.** Remove an annotation, a bean or a config line. I diagnose from the stack trace and logs before you explain.
- Hint ladder: when I am stuck, give a hint first, then a bigger hint, then the answer.

### Quirk drills

- Each phase has about five "predict the output / predict the behavior" snippets. I answer before running them.
- Cover both core Java and Spring. The pool, roughly ordered by phase:
  - **Core Java:** `==` vs `equals` (including the `Integer` cache from -128 to 127), `String` immutability, `equals`/`hashCode` contract in a `HashSet`, checked vs unchecked exceptions, `Optional` misuse (`get()`, `Optional` as a field), records and immutability, generics and type erasure, `List.of` being immutable, `ConcurrentModificationException`, `final` vs effectively final in lambdas, stream laziness, autoboxing `NullPointerException`, `BigDecimal` vs `double` for money, `try`-with-resources.
  - **Spring:** `@Transactional` on a private method or via self-invocation (proxy bypass), checked exceptions not rolling back by default, `LazyInitializationException` and the N+1 query problem, open-session-in-view, field vs constructor injection, two beans of the same type (`NoUniqueBeanDefinitionException`, `@Primary`/`@Qualifier`), bean scopes (singleton holding state), `@Value` property resolution and profiles, `@Valid` missing so validation silently does nothing, `@RestController` vs `@Controller` (JSON vs view name), Spring Security filter ordering and why a request returns 401 vs 403, CSRF blocking `fetch()` POSTs, Jackson infinite recursion on bidirectional entities, `ddl-auto` fighting Flyway, Testcontainers vs `@MockBean` vs `@WebMvcTest` slices.

### End of every phase

- A guided code walkthrough of the whole phase (see "Guided code walkthrough" below), before the checkpoint.
- Git branch cleanup, guided step by step (I run the commands): `git switch main` and `git pull`; `git fetch --prune` to drop references to deleted remote branches; `git branch --merged main` to list merged local branches, then `git branch -d <name>` for each (never `-D` without asking me); `git branch -r` / `git ls-remote --heads origin` to spot leftover remote branches, then `git push origin --delete <name>` only for ones already merged. Explain each command and flag, and confirm with me before any delete.
- Checkpoint against concrete completion criteria.
- Resources.
- An interview drill of 8-12 questions, asked one at a time and graded. Mix Java/Spring and Docker/K8s questions as relevant.
- A prompt to write `docs/notes-phaseN.md` in my own words.
- Revise this plan if practice shows the scope or order should change.

## Instructions for the tutor

When I say **"I'm starting phase N"**, provide:

1. Objectives.
2. Prerequisite checks for tools and the previous phase's state.
3. A concise concept primer (mental models, the problem each concept solves).
4. New or changed code and boilerplate, heavily commented. Show diffs for existing files.
5. A guided tour of the most important lines.
6. Experiments using `change -> predict -> run -> explain`.
7. Java exercises (TDD kata, extend, break-it) and quirk drills.
8. Infrastructure break-it labs.
9. Guided code walkthrough of the phase (see below).
10. Completion criteria.
11. Resources.
12. Interview drill, one question at a time.
13. Notes prompt.

Always diagnose failures first. Do not generate the whole repository in advance; generate files when their phase begins.

Pacing:

- One idea and one action per message. Never stack several new steps in one reply.
- Before each action, explain in plain words what we are doing and why, and where it sits in the bigger picture.
- After each step, ask a short check question and wait for my answer before moving on.
- Assume I am new to Docker/K8s internals. Prefer simple diagrams and plain language over jargon.

Guided code walkthrough (end of every phase):

- Goal: see every part built this phase working together, by following one real request or flow from start to finish.
- Start with a simple ASCII map of the parts involved (for example client -> container -> Spring controller -> service -> repository -> DB) and say which files each box lives in.
- Then trace the flow one stop at a time: open the file, point to the exact lines, and explain in plain words what happens there and why it exists.
- Show it running at each stop where possible: a curl, a log line, a debugger breakpoint (nvim-dap), `docker logs`, or `kubectl describe`. I run the commands myself.
- Before moving to the next stop, ask me to predict what happens next, then wait.
- Connect each stop to the concept from the primer it demonstrates, and to earlier phases where relevant.
- One stop per message. End with a short recap of the map and ask me to explain the whole flow back in my own words.

Reading gate (every time new or changed code lands):

- I tend to skip reading code. Do not continue the lesson until I have actually read what was just added.
- Give me a short reading guide: which files to open, in what order, and which lines or sections to focus on. At minimum, read all the comments.
- Give 2-4 concrete pointers: what to look for, what is surprising or important, and how it connects to the concept we just covered.
- Include nvim moves for the reading: how to open the file and jump to each spot. Base them on my config in `~/.config/nvim` (read `lua/config/keymaps.lua`, `lua/plugins/flash.lua`, `lua/plugins/mini.lua`, `lua/plugins/treesitter.lua`, `lua/config/snacks/keymaps.lua`; leader is Space, hardtime.nvim is on). Teach one or two new moves per step, explain each key, and reuse earlier ones so they stick.
- Known config quirk: `vim.g.no_plugin_maps = true` disables matchit, so `%` does not jump between XML/HTML tags (only brackets). Use `vat` then `o` for tag blocks instead.
- I use a Swedish keyboard on a Mac. For any key that is a symbol, write the physical keystroke too, for example `%` (Shift+5), `/` (Shift+7), `$` (Option+4), `{`/`}` (Shift+Option+8/9), `[`/`]` (Option+8/9), `;` (Shift+,), `:` (Shift+.), `"` (Shift+2), `|` (Option+7), `\` (Shift+Option+7), `` ` `` and `^`/`~` (dead keys: press Space after). Prefer moves that are easy on this layout (letters, Space-leader maps, flash `-`/`_`, `<leader>ff`, `gd`, `grr`, `vat`/`o`, `*`). Avoid or flag `[`/`]` motions as awkward. If a move doesn't work, suspect my config before my typing.
- Then ask 1-3 short check questions that can only be answered by reading the code (for example "which annotation makes this a REST endpoint?", "what port does the container expose, and where is that set?"). Not answerable from the chat alone.
- Wait for my answers. If they are vague or wrong, point me back to the specific file and lines and ask again. Move on only when the answers show I read it.
- Keep the gate light for trivial changes (one line, a config value): one pointer, one question.

Rules for commands I am asked to run:

- Explain every flag and argument (for example `-d`, `-e`, `-p`) and what the command does as a whole.
- When I must predict the output, the flag explanations must not give away the answer. Explain what each flag does, not the consequence being tested. Put the prediction question before the explanation.
- Pull images explicitly (`docker pull ...`) as a separate step before any `docker run`, `kind load` or manifest that uses them, so pull and auth problems surface on their own.

## The project: provisio

A small access-provisioning system, following my boss's sketch. Users in the GUI create and manage **projects** and their **records**. What a user may do depends on their role.

```text
Browser
  │  (HTML pages + fetch()/AJAX)
  ▼
gui ──────────────► provisioning-api ──────────────► execution-api
Spring MVC           Spring Security, RBAC            data operations only
Thymeleaf            "may this user do this?"         (REST + one SOAP endpoint)
                         │                                  │
                         ▼                                  ▼
                   SQL Server db: iam               SQL Server db: appdata
                   users, roles, permissions        projects, records
```

### Responsibilities

- **gui**
  - Classic MVC: Controller -> Model -> Thymeleaf view.
  - Login form.
  - Uses `fetch()` for in-page actions without reloads (AJAX).
  - Never talks to a database.
- **provisioning-api**
  - Authenticates the user and checks role and permission in `iam`.
  - Forwards allowed requests to `execution-api` with a service-to-service token.
  - Returns 401/403 otherwise.
  - Has no access to `appdata`.
- **execution-api**
  - Only data operations: create, read, update, delete.
  - No business rules. Trusts only `provisioning-api`.
  - Exposes REST, plus one small SOAP endpoint (Spring-WS) to cover "WebServices".
  - Has no access to `iam`.

### Roles (initial)

| Role | Permissions |
| --- | --- |
| VIEWER | Read projects and records |
| EDITOR | VIEWER, plus create and update |
| ADMIN | EDITOR, plus delete and assign roles to users |

### Database decision

- One SQL Server 2022 instance with two databases, `iam` and `appdata`.
- A separate SQL login for each service, each with rights to only its own database. This enforces the split at the database level, not just by convention.
- Schema is managed by Flyway migrations in each service. Hibernate `ddl-auto` is set to `validate`, never `update`.

### Stack

- Java 21, Spring Boot 4.1.x (3.x reached end of open-source support in June 2026), Maven multi-module build with a parent POM.
- Spring Web MVC, Thymeleaf, Spring Security, Spring Data JPA, plus one plain JDBC (`JdbcClient`) example for comparison.
- Flyway, Spring-WS, springdoc-openapi, Spring Boot Actuator.
- JUnit 5, Mockito, AssertJ, `@WebMvcTest`/`@DataJpaTest` test slices, Testcontainers (`mssqlserver` module).
- Editor: Neovim with nvim-jdtls (the Eclipse JDT language server) and nvim-dap. jdtls covers the "Eclipse or equivalent" requirement.

### Repository layout (monorepo, grows per phase)

```text
devOps_practise/
  pom.xml                      parent POM: Java version, dependency management, modules
  gui/
  provisioning-api/
  execution-api/
  compose.yaml
  kind-config.yaml
  k8s/
  chart/provisio/
  argocd/
  .github/workflows/
  Makefile
  docs/
    adr/                       architecture decision records
    notes-phaseN.md
    training/                  phase 9 training module
  MASTER_PLAN.md
```

### Tools

- JDK 21 (Temurin) and Maven. Use the Maven wrapper `./mvnw` in the repo.
- Docker Desktop with Rosetta emulation enabled.
- kind, kubectl, k9s, Helm, hey, jq.
- Later: Argo CD and Argo Rollouts CLIs.
- Neovim with nvim-jdtls, nvim-dap and java-debug (installed via Mason).

## Phases

| # | Java / architecture | Docker / K8s / DevOps |
| --- | --- | --- |
| 0 | ✅ Done | Container basics: namespaces, cgroups, layers, `docker run`; repo setup, branch protection, Makefile |
| 1 | Setup checks (below). Maven parent and modules. Build `execution-api` test-first: REST CRUD for projects/records, JPA entities, Flyway, SQL Server via Testcontainers | SQL Server spike (below). Multi-stage Java Dockerfile, naive vs optimized (layered jar), JVM memory in containers (`MaxRAMPercentage`), SQL Server and `execution-api` in Compose with healthchecks |
| 2 | `provisioning-api`: Spring Security, `iam` schema (users, roles, permissions), RBAC checks, calls `execution-api` with `RestClient`, service-to-service token, error mapping (401/403/404/409) | Compose networks and DNS, startup order, per-service database logins created by an init script. CI in GitHub Actions: `./mvnw verify` with Testcontainers, image build, Trivy scan, push to GHCR tagged with the commit SHA |
| 3 | `gui`: Spring MVC controllers, Thymeleaf views, login, `fetch()` for in-page create/delete, CSRF handling | Whole system runs with `docker compose up`. Make targets for build, up, down and logs |
| 4 | SOAP endpoint on `execution-api` (Spring-WS, XSD-first), OpenAPI docs for the REST APIs, first ADRs (why three services, why two databases on one instance, REST vs SOAP) | Small infrastructure increment: call the SOAP endpoint from a container with curl |
| 5 | None | Kubernetes core on kind: SQL Server as a StatefulSet with a PVC, three Deployments, Services and DNS, ConfigMaps, a Secret per service login. kubeconform in CI |
| 6 | Actuator liveness/readiness health groups, graceful shutdown | Production patterns: startup/liveness/readiness probes (slow JVM start), requests and limits, QoS, Ingress, HPA with metrics-server, rolling update and rollback, Flyway as a Job or init container, PDB, node drain. CI policy gate: no root, limits required, no `:latest` |
| 7 | Spring profiles per environment | Helm chart for all three services. Argo CD syncs from Git and self-heals manual edits |
| 8 | None | Argo Rollouts blue/green for `provisioning-api` with active and preview Services, promotion and rollback |
| 9 | A 60-minute "Spring Boot from zero" training module in `docs/training/`, using this project as the example. Covers "kunna hålla utbildning" | Interview prep: mixed Java + K8s mock interview, live debugging drill, architecture walkthrough, README that reproduces everything, three STAR stories |
| 10 | None | Optional: run the same chart on AKS with Azure SQL for about two hours, then delete it the same day |

## Phase 1 setup checks

Run these before writing any Java. Each check has a clear pass condition.

### Toolchain

| Check | Command | Pass |
| --- | --- | --- |
| JDK | `java -version` | Shows 21.x |
| Maven | `./mvnw -v` (after the skeleton exists) | Shows Maven 3.9+ running on Java 21 |
| Docker arch | `docker info --format '{{.Architecture}}'` | Shows `aarch64`. Confirm Rosetta emulation is enabled in Docker Desktop settings |

### nvim-jdtls

Open `execution-api/src/main/java/.../ProjectController.java` in Neovim.

| # | Check | Pass |
| --- | --- | --- |
| 1 | `:checkhealth` and `:Mason` | jdtls, java-debug-adapter and java-test are installed |
| 2 | `:LspInfo` (or `:checkhealth vim.lsp`) | A `jdtls` client is attached to the buffer, with the repo root as root dir |
| 3 | Go to definition on a Spring annotation such as `@RestController` | Jumps into the decompiled or source jar. This proves Maven dependencies are resolved |
| 4 | Type a deliberate error (`String x = 1;`) | Diagnostic appears without saving |
| 5 | Completion on `List.of(1,2).` | Shows `stream()`, `size()` and other methods |
| 6 | Code action on an unused import, or "organize imports" | Import is removed |
| 7 | Rename a method with LSP rename | All call sites, including tests, are updated |
| 8 | Change a dependency in `pom.xml` | jdtls picks it up. If not, run `:JdtUpdateConfig` (or restart) |
| 9 | Run the nearest test from Neovim (`require('jdtls').test_nearest_method()`) | The test runs and passes |

### nvim-dap walkthrough

This is guided in phase 1, not just a pass/fail check.

1. **Debug a unit test.** Set a breakpoint inside a service method. Debug the nearest test. Step over, step into, step out. Inspect locals in the scopes view. Evaluate an expression in the REPL.
2. **Conditional breakpoint.** Break only when `projectId == 2`.
3. **Debug the running app.**
   - Start `execution-api` with `./mvnw spring-boot:run` and JDWP enabled on port 5005.
   - Attach nvim-dap (an "attach" configuration on localhost:5005).
   - Send a request with curl and hit the breakpoint inside the controller.
4. **Debug inside a container.**
   - Run the container with `JAVA_TOOL_OPTIONS` enabling JDWP and publish port 5005.
   - Attach from Neovim.
   - Discuss why this must never ship to production.
5. **Find a bug with the debugger.** I plant a bug that the tests do not catch. You find it using breakpoints only, without reading the diff.

### SQL Server spike (about 15 minutes)

Do this first so a platform problem shows up before any code depends on it.

1. Pull `mcr.microsoft.com/mssql/server:2022-latest` with `docker pull --platform linux/amd64`, then run it with `docker run --platform linux/amd64`. Connect with `sqlcmd` inside the container and run `SELECT @@VERSION`.
2. Create a kind cluster and run the same image as a pod. Check that it reaches Ready and accepts a query.
3. If step 2 fails or is unstable:
   - Fall back to running SQL Server as a plain container outside the cluster.
   - Expose it to the cluster with a selector-less Service and an EndpointSlice, mirroring an external managed database.
   - Record the decision as an ADR.

## Decisions and scope boundaries

- Database: SQL Server, because the role asks for it. One instance, two databases, one login per service.
- Azure SQL Edge (the old ARM-friendly option) was retired in 2025. Use the official amd64 image under emulation.
- CI/CD: GitHub Actions. Registry: GHCR.
- Auth: Spring Security with users stored in `iam`. No OAuth, Keycloak or JWT identity provider.
- Phase 8 is blue/green only. Canary with automated analysis (needs Prometheus) is an optional extension.
- Cut from the core plan: Redis, message queues and Kafka, service mesh, Terraform, preview environments, multi-cluster, Vault, full observability stack, frontend frameworks (no React; Thymeleaf + `fetch()` is the point).
- Local-first on kind. AKS only in optional phase 10.

## Resources

- Spring Boot reference: https://docs.spring.io/spring-boot/
- Spring guides (short, hands-on): https://spring.io/guides
- Spring Security reference: https://docs.spring.io/spring-security/reference/
- Baeldung (practical Spring/Java articles): https://www.baeldung.com/
- Testcontainers for Java: https://java.testcontainers.org/
- Flyway: https://documentation.red-gate.com/flyway
- SQL Server on Linux containers: https://learn.microsoft.com/sql/linux/sql-server-linux-docker-container-deployment
- nvim-jdtls: https://github.com/mfussenegger/nvim-jdtls
- nvim-dap: https://github.com/mfussenegger/nvim-dap
- Book: _Effective Java_ by Joshua Bloch (the source of many quirk drills)
- Book: _Spring Boot: Up and Running_ by Mark Heckler
- Kubernetes docs: https://kubernetes.io/docs/home/
- Docker docs: https://docs.docker.com/
- kind: https://kind.sigs.k8s.io/
- Helm: https://helm.sh/docs/
- Argo CD: https://argo-cd.readthedocs.io/
- Argo Rollouts: https://argoproj.github.io/argo-rollouts/
- Book: _Kubernetes Up & Running_ by Burns, Beda and Hightower

## Starting point

In a new chat opened in this folder, say:

> I'm starting phase 1 now. Use MASTER_PLAN.md as context. Begin with the setup checks and the SQL Server spike, then generate the Maven skeleton and execution-api, and guide me through it.
