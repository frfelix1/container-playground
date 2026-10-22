# linkr - the image used in phase 0. Phase 1 compares it against Dockerfile.naive.
# Build from repo root:  docker build -f docker/Dockerfile -t linkr:dev .

# Slim = Debian minus docs/man pages/compilers. ~50MB vs ~400MB for python:3.12.
FROM python:3.12-slim

# Don't write .pyc files; don't buffer stdout (so `docker logs` is real-time).
ENV PYTHONDONTWRITEBYTECODE=1 PYTHONUNBUFFERED=1

WORKDIR /app

# Copy ONLY requirements first. Each instruction is a cached layer; this layer
# is only rebuilt when requirements.txt changes, not on every code edit.
COPY app/requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Code changes often -> goes last so it invalidates as few layers as possible.
COPY app/ .

# Don't run as root. If the process is compromised, it's an unprivileged user.
RUN useradd --uid 10001 --no-create-home linkr
USER 10001

# Documentation only - EXPOSE does NOT publish the port. `-p` does that.
EXPOSE 8000

# Exec form (JSON array): uvicorn becomes PID 1 and receives SIGTERM directly.
# Shell form (`CMD uvicorn ...`) would make /bin/sh PID 1 - phase 1 shows why that hurts.
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]
