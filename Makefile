# Thin wrapper over commands you should also be able to type by hand.
# Grows each phase. `make help` lists targets.

.PHONY: help

help: ## list targets
	@grep -E '^[a-z-]+:.*##' $(MAKEFILE_LIST) | awk -F':.*## ' '{printf "  %-8s %s\n", $$1, $$2}'
