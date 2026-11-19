# ADR 0001: Run SQL Server as amd64 under emulation on Apple Silicon

Status: accepted (2026-11-19)

## Context

The role this project targets uses SQL Server, so the database stays SQL Server.
The official image `mcr.microsoft.com/mssql/server` is published for amd64 only.
Development happens on an Apple Silicon Mac (arm64).
Azure SQL Edge, the old ARM-friendly alternative, was retired in 2025.

## Decision

Run the official amd64 image under Docker Desktop's Rosetta emulation:
`platform: linux/amd64` in `compose.yaml`. Tests use the same image through Testcontainers.
CI runs on amd64 Linux runners, where no emulation is needed.

## Consequences

- One image everywhere: laptop, tests and CI run the same SQL Server.
- Slow on the Mac: startup takes longer, so the Compose healthcheck has a 30s `start_period`.
- Requires Rosetta to be enabled in Docker Desktop.
- Emulation bugs are possible on the Mac. If SQL Server becomes unstable here
  (for example in kind later), fall back to SQL Server as a plain container outside the cluster.
