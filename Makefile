MILL = ./mill

ALGORITHM  = quick-sort
ELEMENTS   = 20
MAX_SIZE   = 100
DELAY_MS   = 100
DATA_STRUCT = array

.DEFAULT_GOAL := help

.PHONY: compile
compile: ## Compile all sources
	$(MILL) visualizer.compile

.PHONY: help
help: ## Show help for all targets
	@echo "Available targets:"
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-20s\033[0m %s\n", $$1, $$2}'

.PHONY: run
run: ## Run in headless mode (quicksort, $(ELEMENTS) elements)
	$(MILL) visualizer.run \
	  --algorithm $(ALGORITHM) \
	  --number-of-elements $(ELEMENTS) \
	  --max-element-size $(MAX_SIZE) \
	  --delay-between-steps-ms $(DELAY_MS) \
	  --data-structure $(DATA_STRUCT)

.PHONY: run-gui
run-gui: ## Run with bar-chart GUI (quicksort, $(ELEMENTS) elements)
	$(MILL) visualizer.run \
	  --algorithm $(ALGORITHM) \
	  --number-of-elements $(ELEMENTS) \
	  --max-element-size $(MAX_SIZE) \
	  --delay-between-steps-ms $(DELAY_MS) \
	  --data-structure $(DATA_STRUCT) \
	  --gui

.PHONY: test
test: ## Run all tests
	$(MILL) __.test
