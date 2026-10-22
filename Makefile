# Thin wrapper over commands you should also be able to type by hand.
# Grows each phase. `make help` lists targets.
IMAGE ?= linkr
TAG   ?= dev

.PHONY: help build run clean

help: ## list targets
	@grep -E '^[a-z-]+:.*##' $(MAKEFILE_LIST) | awk -F':.*## ' '{printf "  %-8s %s\n", $$1, $$2}'

build: ## build the API image
	docker build -f docker/Dockerfile -t $(IMAGE):$(TAG) .

run: ## run API alone (no deps -> /readyz will say 503)
	docker run --rm -p 8000:8000 --name linkr-api $(IMAGE):$(TAG)

clean: ## remove phase-0 containers and network
	-docker rm -f linkr-api linkr-db linkr-redis
	-docker network rm linkr-net
