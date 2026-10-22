# Context Handoff: Docker & Kubernetes Learning Project

## Who I am and the goal

- I have a strong DevOps background, including CI/CD, Linux, cloud and Azure DevOps Expert-level knowledge.
- I have very little practical Docker/Kubernetes experience.
- The primary goal is to build real practical understanding of Docker and Kubernetes so I can list them on my CV and answer technical interview questions honestly.
- The secondary goal is to keep my DevOps skills sharp by running the project like a real team would.
- A DevOps addition belongs in the project only when it touches Docker or Kubernetes.
- AI-driven development and spec-driven development are not learning goals for this project. AI may generate boilerplate, but the project is not about learning AI-assisted programming.

## Learning approach

- Do not make me write YAML or Dockerfiles from scratch. Generate working, heavily commented boilerplate.
- Teach through a `change -> predict -> run -> explain` loop. Ask me to predict the outcome and wait for my answer before revealing it.
- Prefer side-by-side comparisons, such as a naive and optimized Dockerfile or Postgres as a Deployment versus a StatefulSet.
- Use break-it labs. Tell me how to break something, but not what symptom to expect. Have me diagnose it with `describe`, `logs` and `events` before explaining it.
- End each phase with a checkpoint, resources, an interview drill with questions asked one at a time, and a prompt to write `docs/notes-phaseN.md` in my own words.
- Everything should run locally on kind so it is free and cannot accidentally incur cloud costs.
- Revise the plan between phases when practical experience shows that the scope or order should change.
- Keep the project small enough that the infrastructure remains the subject.

## Instructions for the tutor

When I say **"I'm starting phase N"**, provide:

1. Objectives.
2. Prerequisite checks for tools and the previous phase's state.
3. A concise concept primer with mental models, the problem each concept solves, and relevant Linux or networking mechanisms.
4. New or changed boilerplate, heavily commented. Show diffs for existing files.
5. A guided tour of the most important lines and fields.
6. Experiments using `change -> predict -> run -> explain`.
7. Break-it labs that require diagnosis before explanation.
8. Concrete completion criteria.
9. Specific documentation and other resources.
10. An 8-12 question interview drill, asked one question at a time and graded.
11. A prompt for writing the phase notes.

Always diagnose failures first. Prefer `docker inspect`, `docker logs`, `kubectl describe`, `kubectl logs` and Kubernetes events before suggesting fixes.

## The project: linkr

`linkr` is a small Python URL shortener used to learn Docker and Kubernetes.

| Component   | Technology                    | What it teaches                                                     |
| ----------- | ----------------------------- | ------------------------------------------------------------------- |
| API         | Python 3.12, FastAPI, uvicorn | Stateless workload, Deployment, scaling and probes                  |
| Database    | PostgreSQL 16                 | State, volumes, StatefulSet and Secrets                             |
| Cache/queue | Redis 7                       | Service discovery, dependencies, caching and queues                 |
| Worker      | Python script                 | Non-HTTP workload that consumes click events and updates PostgreSQL |

API endpoints:

- `POST /shorten`
- `GET /{code}`: returns a 307 redirect, uses Redis and emits a click event
- `GET /stats/{code}`
- `GET /healthz`: liveness endpoint
- `GET /readyz`: readiness endpoint that checks PostgreSQL and Redis
- `GET /debug/burn?seconds=N`: optional CPU load endpoint for HPA experiments

The app also supports `CRASH_ON_START=true` for failure labs. Configuration uses `DATABASE_URL`, `REDIS_URL`, `LOG_LEVEL` and `APP_VERSION`.

Expected repository layout:

```text
devOps_practise/
  app/
  docker/
    Dockerfile.naive
    Dockerfile
  .dockerignore
  compose.yaml
  kind-config.yaml
  k8s/
    base/
    variants/
  chart/linkr/
  argocd/
  .github/workflows/
  Makefile
  docs/
  MASTER_PLAN.md
```

Tools: Docker Desktop or OrbStack, kind, kubectl, k9s, Helm, hey, jq, and later the Argo CD and Argo Rollouts CLIs.

## Phases

| Phase                    | Docker/Kubernetes focus                                                                                                                                                   | DevOps thread                                                                                    |
| ------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------ |
| 0. Setup                 | Starter kit, plain `docker run`, and what a container is: a process with namespaces, cgroups and a layered filesystem                                                     | Repository setup, branch protection and a Makefile                                               |
| 1. Docker                | Naive versus optimized images, image size and layers, cache behavior, PID 1 and signals, Compose, networks, DNS, volumes, healthchecks, resource limits and OOM           | GitHub Actions for lint, tests, image build, Trivy scan and GHCR push tagged with the commit SHA |
| 2. Kubernetes core       | Cluster architecture, Deployment -> ReplicaSet -> Pod, Services and DNS, StatefulSet versus Deployment, ConfigMap, Secret, PVC, kubectl, k9s and failure diagnosis        | CI validates manifests with kubeconform                                                          |
| 3. Production patterns   | Liveness/readiness/startup probes, requests and limits, QoS, Ingress, HPA, metrics-server, rolling updates, rollback, Jobs, CronJobs, init containers, PDB and node drain | CI policy gate: no root user, resource limits required and no `:latest`                          |
| 4. Helm and GitOps       | Raw manifests versus Helm, templating, values per environment, install, upgrade and rollback                                                                              | Argo CD syncs from Git and self-heals manual cluster edits                                       |
| 5. Progressive delivery  | Argo Rollouts blue/green deployment using active and preview Services, promotion and rollback                                                                             | Automated promotion and rollback                                                                 |
| 6. Interview preparation | Mixed mock interview, live debugging drill, architecture diagram, reproducible README and three STAR stories                                                              | None beyond documenting the work                                                                 |
| 7. Optional AKS          | Run the same chart on real AKS for roughly two hours, then delete it the same day                                                                                         | Learn cloud load balancer, storage and managed-control-plane differences                         |

### Phase 5 optional extension

Canary delivery with automated analysis is an optional follow-up if the core project is complete and I want more depth. It may require Prometheus and should not be allowed to expand the main project by default.

## Decisions and scope boundaries

- CI/CD platform: GitHub Actions.
- Image registry: GitHub Container Registry.
- Phase 5: blue/green only by default.
- Cloud: local-first. AKS is the final optional phase and does not need detailed planning yet.
- Do not use LocalStack as an AKS substitute. It targets AWS and does not reproduce the real cloud wiring that makes a managed Kubernetes exercise useful.
- Cut from the core plan: service mesh, Terraform, preview environments, multi-cluster setups, Vault and a full observability stack. Prometheus is allowed only if I choose the optional canary extension.
- Do not generate the entire repository in advance. Generate files when the relevant phase begins so the plan can be revised between phases.

## Resources

- Kubernetes documentation: https://kubernetes.io/docs/home/
- Docker documentation: https://docs.docker.com/
- kind: https://kind.sigs.k8s.io/
- Helm: https://helm.sh/docs/
- Argo CD: https://argo-cd.readthedocs.io/
- Argo Rollouts: https://argoproj.github.io/argo-rollouts/
- k9s: https://k9scli.io/
- Interactive labs: https://killercoda.com/ and https://labs.iximiuz.com/
- Book: _Docker Deep Dive_ by Nigel Poulton
- Book: _Kubernetes Up & Running_ by Burns, Beda and Hightower
- Videos: TechWorld with Nana's Docker and Kubernetes crash courses

## Starting point

In a new chat opened in this folder, say:

> I'm starting phase 0 now. Use MASTER_PLAN.md as context. Generate only the starter kit needed for phase 0 and phase 1, and guide me through it using the change -> predict -> run -> explain workflow.
