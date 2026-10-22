# Smoke test for CI (phase 1). Needs no Postgres/Redis: /healthz is dependency-free.
from fastapi.testclient import TestClient

from main import app


def test_healthz():
    r = TestClient(app).get("/healthz")
    assert r.status_code == 200
    assert r.json()["status"] == "ok"
