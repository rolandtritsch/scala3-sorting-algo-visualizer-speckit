# Feature Specification: Sorting Algorithm Visualizer CLI

**Feature Branch**: `001-sorting-visualizer`
**Created**: 2026-03-02
**Status**: Draft
**Input**: User description: "Build a CLI sorting algorithm visualizer with optional GUI bar chart display"

## Clarifications

### Session 2026-03-02

- Q: When `--gui` is active, does the tool also write INFO log messages to the terminal, or does the GUI replace terminal output entirely? → A: Both — GUI updates and terminal INFO logs run concurrently.
- Q: What counts as one "step" in quick sort for logging and timing purposes? → A: One swap/placement — each time an element is moved to its final or intermediate position (consistent with bubble sort granularity).
- Q: What must the INFO log message per step include? → A: INFO logs step index + swapped/moved positions; DEBUG logs a full collection snapshot after each step.
- Q: Should `--number-of-elements 0` be invalid input or a valid empty collection? → A: Valid — 0 elements is accepted; the tool completes immediately with 0 steps and exits cleanly.
- Q: If `--gui` is passed on a machine with no display, how should the tool behave? → A: Error — tool detects no display, prints a clear error message, and exits with a non-zero code.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Basic CLI Sort with Progress Logging (Priority: P1)

A developer invokes the tool from the command line, specifying the number of elements, element
value range, algorithm, data structure, and delay. The tool generates a data structure populated
with random integers and sorts it step by step, emitting one INFO log message per step to show
progress. When sorting completes, it prints elapsed time and net execution time.

**Why this priority**: Core functionality—sorting with observable progress logging—delivers
immediate value without the GUI and validates both algorithm implementations.

**Independent Test**: Can be fully tested by running the tool without `--gui` and verifying INFO
logs appear for each step and timing info is printed at the end.

**Acceptance Scenarios**:

1. **Given** valid CLI parameters (no `--gui`), **When** the tool runs, **Then** it generates a
   data structure of the correct size with values in `[0, max-element-size]` (0 elements allowed),
   sorts it using the specified algorithm, emits one INFO log per step, and prints elapsed and
   execution times on completion.
2. **Given** `--delay-between-steps-ms 100`, **When** the tool runs, **Then** there is a visible
   pause of approximately 100 ms between each sorting step.
3. **Given** `--algorithm quick-sort`, **When** the tool runs, **Then** the data structure is
   fully sorted in ascending order at the end.
4. **Given** `--algorithm bubble-sort`, **When** the tool runs, **Then** the data structure is
   fully sorted in ascending order at the end.
5. **Given** DEBUG logging is enabled, **When** the tool runs, **Then** a full collection snapshot
   appears at DEBUG level after each step, in addition to the INFO step messages.
6. **Given** `--number-of-elements 0`, **When** the tool runs, **Then** it exits cleanly with
   zero steps reported and elapsed/execution times of 0 ms.

---

### User Story 2 - GUI Bar Chart Visualization (Priority: P2)

A developer adds the `--gui` flag to observe sorting visually. A window opens in the center of
the screen and renders the data structure as a bar chart, updating the display after every sorting
step. Terminal INFO log messages continue to appear concurrently. Once sorting finishes, the
window remains open until the user presses ESC to close it.

**Why this priority**: The GUI is an optional enhancement that provides a richer learning
experience, but the tool still delivers value without it (P1 covers core functionality).

**Independent Test**: Can be fully tested by running with `--gui` on a small dataset and
confirming the bar chart updates correctly per step, terminal logs still appear, and the window
closes on ESC.

**Acceptance Scenarios**:

1. **Given** the `--gui` flag is passed, **When** the tool starts, **Then** a window opens
   centered on the screen displaying the unsorted data as a vertical bar chart.
2. **Given** the GUI window is open, **When** a sorting step occurs, **Then** the bar chart
   updates to reflect the current state of the data structure **and** an INFO log message is
   emitted to the terminal.
3. **Given** sorting has completed, **When** the user presses ESC, **Then** the window closes
   cleanly.
4. **Given** the `--gui` flag is **not** passed, **When** the tool runs, **Then** no window is
   opened.
5. **Given** `--gui` is passed on a machine with no display available, **When** the tool starts,
   **Then** it prints a descriptive error message and exits with a non-zero status code.

---

### User Story 3 - Timing and Performance Reporting (Priority: P3)

After sorting completes, the tool prints two timing metrics as INFO log messages: (1) total
elapsed time in milliseconds, and (2) net execution time computed as elapsed time minus the total
accumulated delay (`delay-between-steps-ms × number-of-steps`).

**Why this priority**: Timing data gives insight into algorithm efficiency independent of
artificial delay, but it depends on the core sort (P1) being completed first.

**Independent Test**: Can be fully tested by running with a known delay and step count, verifying
elapsed ≈ (execution time + delay × steps) within a reasonable tolerance.

**Acceptance Scenarios**:

1. **Given** sorting completes with `--delay-between-steps-ms 50` and N steps, **When** the
   summary is printed, **Then** the elapsed time is at least `50 × N` ms and the execution time
   equals `elapsed − (50 × N)` ms.
2. **Given** `--delay-between-steps-ms 0`, **When** the summary is printed, **Then** elapsed time
   and execution time are equal (or differ by less than 5 ms due to timer precision).

---

### User Story 4 - Project Documentation (Priority: P4)

A developer reading the repository understands what the tool does and can build, run, and
contribute to it using only the provided documentation files.

**Why this priority**: Documentation is important for maintainability but does not block the
functioning tool; it is the last deliverable.

**Independent Test**: Can be tested independently by verifying each document exists, covers its
assigned scope, and contains no placeholder content.

**Acceptance Scenarios**:

1. **Given** README.md is present, **When** a new user reads it, **Then** they understand what
   the tool does, what each CLI parameter controls, and how to run it with an example command.
2. **Given** CLAUDE.md is present, **When** a developer reads it, **Then** they understand the
   code architecture, module layout, and key design decisions.
3. **Given** CONTRIBUTING.md is present, **When** a contributor reads it, **Then** they know how
   to build, test, and run the tool and how to submit changes.

---

### Edge Cases

- What happens when `--number-of-elements` is 0? (Valid: the tool accepts an empty collection,
  reports 0 steps, and exits cleanly with zero elapsed and execution times.)
- What happens when `--number-of-elements` is 1? (One element is already sorted; sorting
  completes immediately with 0 or 1 steps depending on algorithm.)
- What happens when `--max-element-size` is 1? (All elements equal 1; sorting completes
  immediately with no swaps for bubble sort.)
- What happens when `--delay-between-steps-ms` is 0? (No sleep is performed; elapsed and
  execution times converge to within timer precision.)
- How does the GUI handle very large datasets (e.g., 10,000+ elements)? (Bars may be sub-pixel
  wide; GUI should still render without crashing.)
- What happens if an unsupported `--algorithm` or `--data-structure` value is provided? (Tool
  exits with a clear error message before generating any data.)
- What happens if `--gui` is passed with no display server available? (Tool prints a descriptive
  error message and exits with a non-zero status code before attempting any sorting.)

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The tool MUST accept these CLI parameters: `--number-of-elements` (non-negative
  integer, 0 or greater), `--max-element-size` (positive integer ≥ 1),
  `--delay-between-steps-ms` (non-negative integer), `--algorithm` (one of: `quick-sort`,
  `bubble-sort`), `--data-structure` (one of: `list`, `array`, `vector`), and `--gui` (optional
  boolean flag).
- **FR-002**: The tool MUST generate the specified data structure populated with
  `number-of-elements` random integer values, each in the inclusive range `[1, max-element-size]`.
  Values do not need to be unique. If `number-of-elements` is 0, an empty collection is used.
- **FR-003**: The tool MUST sort the generated data structure in ascending order using the chosen
  algorithm.
- **FR-004**: The tool MUST emit one INFO-level log message per sorting step. Each message MUST
  include the step index and the indices (positions) of the elements swapped or moved during that
  step (e.g., "Step 42: swapped positions 3 and 7").
- **FR-005**: The tool MUST emit a full snapshot of the current collection state as a DEBUG-level
  log message after each sorting step, in addition to the INFO message. All other lower-level
  diagnostic messages MUST also use DEBUG level so they are hidden by default.
- **FR-006**: The tool MUST pause for `delay-between-steps-ms` milliseconds between each sorting
  step.
- **FR-007**: The tool MUST print an INFO-level summary on completion reporting (a) total elapsed
  time in milliseconds and (b) net execution time in milliseconds, where execution time = elapsed
  time − (delay-between-steps-ms × number-of-steps).
- **FR-008**: When the `--gui` flag is present, the tool MUST open a window centered on the
  screen that renders the data structure as a vertical bar chart, updating the chart after each
  sorting step. Terminal INFO log messages MUST continue to be emitted concurrently.
- **FR-009**: When the GUI window is open and sorting is complete, the tool MUST close the window
  when the user presses the ESC key.
- **FR-010**: When `--gui` is passed on a system where no display is available, the tool MUST
  print a descriptive error message and exit with a non-zero status code before generating any
  data.
- **FR-011**: The tool MUST exit with a non-zero status code and a descriptive error message when
  invalid or missing required parameter values are supplied.
- **FR-012**: The repository MUST include a README.md (purpose and CLI usage), a CLAUDE.md
  (architecture and implementation), and a CONTRIBUTING.md (build, test, and contribution
  workflow).

### Key Entities

- **SortableCollection**: The in-memory data structure (list, array, or vector) holding integer
  elements; tracks current element order across steps. May be empty (0 elements).
- **SortingStep**: A single observable unit of sorting work — one swap or element placement to a
  final or intermediate position; associated with a step index, the positions of moved elements,
  and the current collection state.
- **SortingResult**: The outcome of a completed sort run; contains total elapsed time, step count,
  and computed net execution time.
- **BarChartFrame**: A snapshot of the collection rendered as proportionally-sized bars for GUI
  display; derived from a SortingStep.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can run the tool with any valid combination of parameters and obtain a fully
  sorted output with per-step progress logged, with zero unexplained errors across all supported
  algorithm and data-structure combinations.
- **SC-002**: The reported net execution time excludes artificial delay within a tolerance of ±5 ms,
  verifiable by comparing runs with `--delay-between-steps-ms 0` against runs with a non-zero
  delay on the same dataset.
- **SC-003**: The GUI bar chart reflects the correct element order after every step with no missed
  updates, verifiable on datasets up to 500 elements.
- **SC-004**: All three documentation files (README.md, CLAUDE.md, CONTRIBUTING.md) are present,
  complete, and contain no placeholder text, verifiable by manual review.
- **SC-005**: A developer unfamiliar with the project can build and run the tool successfully
  using only the CONTRIBUTING.md instructions, without needing external guidance.

## Assumptions

- The tool targets developers and learners; no interactive setup wizard is required.
- "Step" is defined uniformly for both algorithms: one swap or element placement to a final or
  intermediate position. For bubble sort this is one conditional swap; for quick sort this is one
  element movement during a partition scan.
- Bar chart bar height is proportional to element value relative to `max-element-size`; bar width
  is determined by window size and dataset size.
- The GUI window has a reasonable default size; resize behavior is out of scope.
- Elements are integers; no overflow concerns given the `[1, max-element-size]` constraint.
- Log output goes to standard output/error; no log file is required.
- The delay is applied uniformly between every step; no adaptive delay is needed.
- `--gui` requires a graphical display environment; the tool does not attempt to fall back
  silently to CLI mode if no display is found.
