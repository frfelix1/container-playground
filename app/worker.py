"""linkr worker - a non-HTTP workload.

Blocks on a Redis list and increments click counters in Postgres.
No port, no HTTP: in Kubernetes this teaches you that not every Deployment
needs a Service, and that liveness must be checked differently.
"""

import logging
import os
import signal
import sys

import psycopg
import redis

DATABASE_URL = os.getenv("DATABASE_URL", "postgresql://linkr:linkr@localhost:5432/linkr")
REDIS_URL = os.getenv("REDIS_URL", "redis://localhost:6379/0")
logging.basicConfig(stream=sys.stdout, level=os.getenv("LOG_LEVEL", "INFO"),
                    format="%(asctime)s %(levelname)s worker %(message)s")
log = logging.getLogger("worker")

running = True


def stop(signum, _frame):
    # Docker/K8s send SIGTERM on stop. If we ignore it, they SIGKILL us after
    # a grace period (10s in Docker, 30s in K8s). Phase 1 makes you watch this.
    global running
    log.info("got signal %s, finishing current item", signum)
    running = False


signal.signal(signal.SIGTERM, stop)
signal.signal(signal.SIGINT, stop)

r = redis.Redis.from_url(REDIS_URL, decode_responses=True)
log.info("worker started (pid %s)", os.getpid())

with psycopg.connect(DATABASE_URL, autocommit=True) as conn:
    while running:
        item = r.brpop("linkr:clicks", timeout=1)  # short timeout so we notice signals
        if item:
            conn.execute("UPDATE links SET clicks = clicks + 1 WHERE code=%s", (item[1],))
            log.debug("click %s", item[1])

log.info("worker exited cleanly")
