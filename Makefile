MILL = ./mill

ALGORITHM  = quick-sort
ELEMENTS   = 20
MAX_SIZE   = 100
DELAY_MS   = 100
DATA_STRUCT = array

.PHONY: compile
compile:
	$(MILL) visualizer.compile

.PHONY: help
help:
	@echo "Usage: make <target>"
	@echo ""
	@echo "  compile   Compile all sources"
	@echo "  test      Run all tests"
	@echo "  run       Run in headless mode (quicksort, $(ELEMENTS) elements)"
	@echo "  run-gui   Run with bar-chart GUI (quicksort, $(ELEMENTS) elements)"

.PHONY: run
run:
	$(MILL) visualizer.run \
	  --algorithm $(ALGORITHM) \
	  --number-of-elements $(ELEMENTS) \
	  --max-element-size $(MAX_SIZE) \
	  --delay-between-steps-ms $(DELAY_MS) \
	  --data-structure $(DATA_STRUCT)

.PHONY: run-gui
run-gui:
	$(MILL) visualizer.run \
	  --algorithm $(ALGORITHM) \
	  --number-of-elements $(ELEMENTS) \
	  --max-element-size $(MAX_SIZE) \
	  --delay-between-steps-ms $(DELAY_MS) \
	  --data-structure $(DATA_STRUCT) \
	  --gui

.PHONY: test
test:
	$(MILL) __.test
