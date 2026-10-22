"""linkr API - a deliberately small URL shortener.

The app is intentionally boring: the infrastructure around it is the subject.
All config comes from environment variables (12-factor style), which is exactly
how Docker (-e / env_file) and Kubernetes (ConfigMap / Secret) will inject it.
"""

import logging
import os
import secrets
import sys
import time
from contextlib import asynccontextmanager

import psycopg
import redis
from fastapi import FastAPI, HTTPException, Response
from fastapi.responses import RedirectResponse
from pydantic import BaseModel, HttpUrl

# --- Configuration (env vars only; defaults suit local `docker run`) ---------
DATABASE_URL = os.getenv("DATABASE_URL", "postgresql://linkr:linkr@localhost:5432/linkr")
REDIS_URL = os.getenv("REDIS_URL", "redis://localhost:6379/0")
LOG_LEVEL = os.getenv("LOG_LEVEL", "INFO")
APP_VERSION = os.getenv("APP_VERSION", "dev")
CLICK_QUEUE = "linkr:clicks"  # Redis list the worker consumes

# Log to stdout: containers should never write logs to files.
# `docker logs` / `kubectl logs` simply read the process's stdout/stderr.
logging.basicConfig(stream=sys.stdout, level=LOG_LEVEL,
                    format="%(asctime)s %(levelname)s %(message)s")
log = logging.getLogger("linkr")

# Failure-lab switch: simulates an app that dies at boot (bad config, missing
# dependency...). Used to practise diagnosing crash loops.
if os.getenv("CRASH_ON_START", "false").lower() == "true":
    log.error("CRASH_ON_START=true -> exiting with code 1")
    sys.exit(1)

cache = redis.Redis.from_url(REDIS_URL, decode_responses=True)


def db():
    # ponytail: one connection per request, add a pool if load testing shows it matters
    return psycopg.connect(DATABASE_URL, autocommit=True)


@asynccontextmanager
async def lifespan(_app):
    # Create the schema on startup. If Postgres isn't reachable yet, log and
    # keep going: /readyz reports not-ready instead of the process crashing.
    try:
        with db() as conn:
            conn.execute("""CREATE TABLE IF NOT EXISTS links (
                code   TEXT PRIMARY KEY,
                url    TEXT NOT NULL,
                clicks BIGINT NOT NULL DEFAULT 0)""")
        log.info("schema ready")
    except Exception as e:
        log.warning("db not reachable at startup: %s", e)
    log.info("linkr %s started (pid %s)", APP_VERSION, os.getpid())
    yield
    # Runs on SIGTERM -> graceful shutdown. Phase 1 explores PID 1 + signals.
    log.info("shutting down cleanly")


app = FastAPI(title="linkr", version=APP_VERSION, lifespan=lifespan)


class ShortenIn(BaseModel):
    url: HttpUrl


@app.get("/healthz")
def healthz():
    """Liveness: 'is the process alive?' Never checks dependencies -
    otherwise a DB outage would make the orchestrator restart healthy apps."""
    return {"status": "ok", "version": APP_VERSION}


@app.get("/readyz")
def readyz(response: Response):
    """Readiness: 'can I serve traffic right now?' Checks dependencies."""
    checks = {}
    try:
        with db() as conn:
            conn.execute("SELECT 1")
        checks["postgres"] = "ok"
    except Exception as e:
        checks["postgres"] = f"fail: {e.__class__.__name__}"
    try:
        cache.ping()
        checks["redis"] = "ok"
    except Exception as e:
        checks["redis"] = f"fail: {e.__class__.__name__}"
    if any(v != "ok" for v in checks.values()):
        response.status_code = 503
    return checks


@app.post("/shorten", status_code=201)
def shorten(body: ShortenIn):
    code = secrets.token_urlsafe(4)
    with db() as conn:
        conn.execute("INSERT INTO links (code, url) VALUES (%s, %s)", (code, str(body.url)))
    return {"code": code, "url": str(body.url)}


@app.get("/debug/burn")
def burn(seconds: float = 1.0):
    """Spin the CPU so the HPA has something to react to (phase 3)."""
    end = time.time() + min(seconds, 30)
    while time.time() < end:
        pass
    return {"burned_seconds": seconds}


@app.get("/stats/{code}")
def stats(code: str):
    with db() as conn:
        row = conn.execute("SELECT url, clicks FROM links WHERE code=%s", (code,)).fetchone()
    if not row:
        raise HTTPException(404, "unknown code")
    return {"code": code, "url": row[0], "clicks": row[1]}


@app.get("/{code}")
def redirect(code: str):
    # Cache-aside: try Redis first, fall back to Postgres, then populate Redis.
    url = cache.get(f"link:{code}")
    if url is None:
        with db() as conn:
            row = conn.execute("SELECT url FROM links WHERE code=%s", (code,)).fetchone()
        if not row:
            raise HTTPException(404, "unknown code")
        url = row[0]
        cache.set(f"link:{code}", url, ex=3600)
    # Don't update Postgres on the hot path - emit an event, the worker does it.
    cache.lpush(CLICK_QUEUE, code)
    return RedirectResponse(url, status_code=307)
