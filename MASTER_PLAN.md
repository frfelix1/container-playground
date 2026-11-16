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

Goal: something to show early, then go deeper. Build the whole system end to end in thin slices first, then add depth. Sprints finish when they're done. There are no deadlines.

### Depth tiers (decides how much time each piece gets)

| Tier | What | How |
| --- | --- | --- |
| **Deep** | Service logic, RBAC, Spring Security, transactions, JPA, tests | I write the core myself, test first. Quirk drills and the hint ladder apply |
| **Medium** | Dockerfile, Compose, K8s manifests, Helm, Argo | AI generates it with comments. I read it, then do one break-it lab per piece |
| **Shallow** | POM boilerplate, CI YAML, README scaffolding, config | AI generates it. I skim it and can say what it does in one sentence |

Rules that stop it from being vibecoded:

- **Explain before merge:** every PR description has 3 lines in my own words: what it does, why, and one thing that could break it. If I can't write them, I don't merge.
- **One break-it per feature**, not one per concept.
- When unsure about a tier, default to medium.

There are three tracks, each with its own style. They apply within the tier rules above.

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

- About five per sprint, and only on deep-tier code. "Predict the output / predict the behavior" snippets that I answer before running them. More on request.
- Cover both core Java and Spring. The pool, roughly ordered by sprint:
  - **Core Java:** `==` vs `equals` (including the `Integer` cache from -128 to 127), `String` immutability, `equals`/`hashCode` contract in a `HashSet`, checked vs unchecked exceptions, `Optional` misuse (`get()`, `Optional` as a field), records and immutability, generics and type erasure, `List.of` being immutable, `ConcurrentModificationException`, `final` vs effectively final in lambdas, stream laziness, autoboxing `NullPointerException`, `BigDecimal` vs `double` for money, `try`-with-resources.
  - **Spring:** `@Transactional` on a private method or via self-invocation (proxy bypass), checked exceptions not rolling back by default, `LazyInitializationException` and the N+1 query problem, open-session-in-view, field vs constructor injection, two beans of the same type (`NoUniqueBeanDefinitionException`, `@Primary`/`@Qualifier`), bean scopes (singleton holding state), `@Value` property resolution and profiles, `@Valid` missing so validation silently does nothing, `@RestController` vs `@Controller` (JSON vs view name), Spring Security filter ordering and why a request returns 401 vs 403, CSRF blocking `fetch()` POSTs, Jackson infinite recursion on bidirectional entities, `ddl-auto` fighting Flyway, Testcontainers vs `@MockBean` vs `@WebMvcTest` slices.

### End of every sprint

- Showcase check: tick off the sprint's items in the "Showcase checklist" below.
- A guided code walkthrough of the sprint (see below), before the checkpoint.
- Git branch cleanup, guided (I run the commands): `git switch main`, `git pull`, `git fetch --prune`, `git branch --merged main`, then `git branch -d <name>` (never `-D` without asking me), and `git push origin --delete <name>` only for merged remote branches. Confirm with me before any delete.
- An interview drill of 8-12 questions, one at a time, graded. Mix Java/Spring and Docker/K8s.
- One STAR story drafted from something that went wrong or was hard this sprint.
- A prompt to write `docs/notes-sprintN.md` in my own words.
- Revise this plan if practice shows the scope or order should change.

## Instructions for the tutor

When I say **"I'm starting sprint N"** (or "continue"), provide:

1. Objectives and the showcase items this sprint delivers.
2. A short recap (3 lines) plus open items from earlier tutor logs.
3. A concise concept primer, only for deep and medium tier topics.
4. Code: generate shallow and medium tier pieces in full, heavily commented. For deep tier pieces, give a failing test or skeleton and let me write the core.
5. One break-it lab per feature, and TDD katas and quirk drills on deep-tier code.
6. Completion criteria.

Always diagnose failures first. Do not generate the whole repository in advance; generate files when their sprint begins.

Pacing:

- Batch by default: one whole step per message (for example "generate the Dockerfile, build it, run it"). Collect the check questions at the end of the step instead of after each action.
- Slow down to one action per message only for deep-tier work, or when I ask.
- Before each step, say in one or two sentences what we're doing, why, and where it fits.
- Assume I am new to Docker/K8s internals. Prefer simple diagrams and plain language over jargon.
- Whenever you ask me to read, open, or edit code, give the full file path and paste the relevant lines (with line numbers). Never make me hunt for files or rely on a handoff note.
- If I push to skip a deep-tier item, name what I'd miss in one line, then follow my call.

Tutor log (continuity across sessions):

- Each sprint has `docs/tutor-log-sprintN.md` (phase 1's log stays `docs/tutor-log-phase1.md`), separate from my own notes.
  The tutor proposes its content; build writes it.
- Whenever the tutor says "later", "we'll cover", or takes a deliberate shortcut,
  it is added to the current tutor log in the same session, with the target sprint.
- At the end of each sprint the tutor log gets: how I did per area (strong / partial / gaps),
  open items carried forward, and things to revisit.
- At the start of every sprint (and every new session), the tutor reads all earlier
  tutor logs and raises any open item that belongs to the current sprint.

Guided code walkthrough (end of every sprint):

- Goal: follow one real request or flow from start to finish through everything built this sprint.
- Start with a simple ASCII map of the parts (for example client -> container -> controller -> service -> repository -> DB) and the files each box lives in.
- Trace the flow stop by stop with exact lines, explaining what happens and why. Show it running where possible (curl, log line, breakpoint, `docker logs`, `kubectl describe`). I run the commands.
- Two or three stops per message. Ask me to predict the next stop at deep-tier stops only.
- End with a short recap and ask me to explain the whole flow back in my own words.

Reading gate (deep and medium tier code only; shallow tier gets one pointer and no questions):

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

### Repository layout (monorepo, grows per sprint)

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
    notes-sprintN.md
    training/                  sprint 4 training module
  MASTER_PLAN.md
```

### Tools

- JDK 21 (Temurin) and Maven. Use the Maven wrapper `./mvnw` in the repo.
- Docker Desktop with Rosetta emulation enabled.
- kind, kubectl, k9s, Helm, hey, jq.
- Later: Argo CD and Argo Rollouts CLIs.
- Neovim with nvim-jdtls, nvim-dap and java-debug (installed via Mason).

## Sprints

Thin end-to-end first, depth after. Each sprint ends with something showable. Phase 0 is done. Phase 1 is nearly done and gets finished inside sprint 1.

| Sprint | Java / architecture | Docker / K8s / DevOps | Showcase output |
| --- | --- | --- | --- |
| 0 | ✅ Done (was phase 0) | Container basics, repo setup, branch protection, Makefile | – |
| 1 | Finish phase 1: close open tutor-log items, REST CRUD for projects/records in `execution-api` (deep) | Multi-stage Dockerfile, layered jar, `MaxRAMPercentage` (medium). SQL Server + `execution-api` in Compose with healthchecks (medium). GitHub Actions: `./mvnw verify`, image build, Trivy, push to GHCR by SHA (shallow) | README with architecture diagram and quick start, CI badge, first ADR |
| 2 | `provisioning-api`: Spring Security, `iam` schema, RBAC rules test-first, `RestClient` to `execution-api`, service token, 401/403/404/409 mapping (deep). Minimal `gui`: login, list, one `fetch()` create with CSRF (medium) | Compose networks/DNS, init script with one SQL login per service | `docker compose up` runs the whole system; demo GIF in README |
| 3 | Actuator health groups, graceful shutdown, Spring profiles (medium) | kind: SQL Server StatefulSet + PVC, three Deployments, Services, ConfigMaps, Secrets, probes, limits, Ingress (medium). Helm chart, Argo CD syncing from Git (medium). kubeconform + policy gate in CI (shallow) | One-command local cluster, GitOps screenshot, ADRs for the K8s choices |
| 4 | SOAP endpoint (Spring-WS, XSD-first), OpenAPI docs (medium). Full `gui` with Thymeleaf views (medium). 60-min "Spring Boot from zero" training module (deep) | HPA, PDB, node drain, Flyway as Job, Argo Rollouts blue/green (medium) | Training module, three STAR stories, mock interview done |
| 5 | Optional | AKS + Azure SQL for about two hours, then delete it the same day | Screenshot and a short write-up |

Within a sprint, the order is flexible. Showcase items count as part of "done".

### Showcase checklist

- [ ] README: what it is, architecture diagram, quick start, tech list, links to ADRs
- [ ] CI badge green on `main`
- [ ] `docs/adr/` with at least 3 ADRs
- [ ] Demo GIF or screenshots (compose and kind)
- [ ] `docs/training/` module
- [ ] Three STAR stories in `docs/interview/` (private if preferred)

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

Optional, on demand. Do it when a real bug needs a debugger, not as a set exercise.

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
- Rollouts are blue/green only. Canary with automated analysis (needs Prometheus) is an optional extension.
- Cut from the core plan: Redis, message queues and Kafka, service mesh, Terraform, preview environments, multi-cluster, Vault, full observability stack, frontend frameworks (no React; Thymeleaf + `fetch()` is the point).
- Local-first on kind. AKS only in optional sprint 5.

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

> I'm starting sprint 1. Use MASTER_PLAN.md and docs/tutor-log-phase1.md as context. Recap where phase 1 stopped, then go.
