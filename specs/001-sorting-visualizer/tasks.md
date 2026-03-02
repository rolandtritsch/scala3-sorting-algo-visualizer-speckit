---

description: "Task list for 001-sorting-visualizer implementation"
---

# Tasks: Sorting Algorithm Visualizer CLI

**Input**: Design documents from `specs/001-sorting-visualizer/`
**Prerequisites**: plan.md ✅, spec.md ✅, research.md ✅, data-model.md ✅, contracts/cli-schema.md ✅, quickstart.md ✅

**Tests**: Included — constitution mandates TDD (failing test before any production code; unit
+ property + edge-case per function).

**Organization**: Tasks are grouped by user story to enable independent implementation and
testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no shared dependencies)
- **[Story]**: Which user story this task belongs to (US1–US4)
- Exact file paths are included in every description

---

## Phase 1: Setup (Build Infrastructure)

**Purpose**: Bootstrap the Mill build system and shared tooling config before any source code
is written.

- [x] T001 Create `build.mill` at repo root defining the `visualizer` Mill module (Scala 3.3.x, scribe 3.15.0, decline 2.6.0, MUnit, ScalaCheck, scalafmt, scalafix plugins)
- [x] T002 Create `.mill-version` at repo root pinning the Mill bootstrap version
- [x] T003 [P] Create `.scalafmt.conf` at repo root (Scala 3 dialect, max column 100)
- [x] T004 [P] Create `.github/workflows/ci.yml` GitHub Actions CI workflow running `./mill __.test` on every push and pull request (FR-012)

**Checkpoint**: `./mill __.test` runs and returns green (empty test suite) — scaffold confirmed.

---

## Phase 2: Foundational (Domain Models + Core Infrastructure)

**Purpose**: Domain models and shared infrastructure that MUST be complete before any user
story can be implemented.

**⚠️ CRITICAL**: No user story work can begin until this phase is complete.

### Domain Models (parallelizable — no cross-dependencies)

- [x] T005 [P] Create `Algorithm` enum in `src/visualizer/domain/model/Algorithm.scala` with values `BubbleSort` (CLI token `bubble-sort`) and `QuickSort` (CLI token `quick-sort`)
- [x] T006 [P] Create `DataStructure` enum in `src/visualizer/domain/model/DataStructure.scala` with values `List` (`list`), `Array` (`array`), `Vector` (`vector`)
- [x] T007 [P] Create `SortableCollection` opaque value class in `src/visualizer/domain/model/SortableCollection.scala` wrapping `IndexedSeq[Int]` with `dataStructure: DataStructure` field; enforce invariant: each element in `[1, maxElementSize]`; allow empty collection
- [x] T008 [P] Create `SortingStep` case class in `src/visualizer/domain/model/SortingStep.scala` with fields `stepIndex: Int`, `movedPositions: (Int, Int)`, `collectionAfter: IndexedSeq[Int]`, `timestamp: Long`
- [x] T009 [P] Create `SortingResult` case class in `src/visualizer/domain/model/SortingResult.scala` with fields `stepCount: Int`, `elapsedMs: Long`, `executionMs: Long` (derived: `elapsedMs - delayBetweenStepsMs * stepCount`), `sortedCollection: IndexedSeq[Int]`

### CLI Parser (TDD — test first)

- [x] T010 Write `CliParserSuite.scala` (failing) in `test/src/visualizer/cli/CliParserSuite.scala` covering: happy-path parse of all flags, missing required flag returns `Left`, out-of-range `--number-of-elements -1` returns `Left`, invalid enum token returns `Left` with user-friendly message, `--delay-between-steps-ms` defaults to 0 when absent
- [x] T011 Implement `CliParser.scala` in `src/visualizer/cli/CliParser.scala` with `decline` `Command[CliConfig]`, `given Argument[Algorithm]`, `given Argument[DataStructure]`, and `CliConfig` case class (all fields from data-model.md); make T010 pass

### Collection Generator (TDD — test first)

- [x] T012 Write `CollectionGeneratorSuite.scala` (failing) in `test/src/visualizer/domain/generator/CollectionGeneratorSuite.scala` covering: generated length equals `numberOfElements`, all elements in `[1, maxElementSize]`, empty collection when `numberOfElements` is 0, ScalaCheck property for size and range invariants
- [x] T013 Implement `CollectionGenerator.scala` in `src/visualizer/domain/generator/CollectionGenerator.scala` generating `SortableCollection` from `CliConfig` using `scala.util.Random`; make T012 pass

**Checkpoint**: Foundation ready — domain models, CLI parser, and generator are tested and
green. User story implementation can now begin.

---

## Phase 3: User Story 1 — Basic CLI Sort with Progress Logging (Priority: P1) 🎯 MVP

**Goal**: Wire the full CLI → generation → algorithm → step logging → delay → timing summary
pipeline. No GUI. Zero unexplained errors across all algorithm and data-structure combinations.

**Independent Test**: Run `./mill visualizer.run --number-of-elements 20 --max-element-size 100 --delay-between-steps-ms 0 --algorithm bubble-sort --data-structure array` and verify INFO logs appear for each step and a completion summary is printed.

### Tests for User Story 1 (TDD — write before implementation)

- [x] T014 [P] [US1] Write `BubbleSortSuite.scala` (failing) in `test/src/visualizer/domain/algorithms/BubbleSortSuite.scala` covering: result is sorted ascending, step count is `>= 0`, each `SortingStep` has valid `movedPositions` indices, empty input produces 0 steps, single-element input produces 0 steps, ScalaCheck property that output is always sorted
- [x] T015 [P] [US1] Write `QuickSortSuite.scala` (failing) in `test/src/visualizer/domain/algorithms/QuickSortSuite.scala` covering: same invariants as BubbleSortSuite (sorted output, valid step indices, empty and single-element edge cases, ScalaCheck sorted property)

### Implementation for User Story 1

- [x] T016 [US1] Implement `BubbleSort.scala` in `src/visualizer/domain/algorithms/BubbleSort.scala` as a pure function `(SortableCollection, CliConfig) => LazyList[SortingStep]` emitting one step per adjacent swap; make T014 pass
- [x] T017 [US1] Implement `QuickSort.scala` in `src/visualizer/domain/algorithms/QuickSort.scala` as a pure function `(SortableCollection, CliConfig) => LazyList[SortingStep]` emitting one step per element placement during partition; make T015 pass
- [x] T018 [US1] Implement `Main.scala` in `src/visualizer/Main.scala` wiring: parse CLI args via `CliParser` (exit code 1 on error), generate `SortableCollection` via `CollectionGenerator`, dispatch to `BubbleSort` or `QuickSort`, for each `SortingStep` emit `[INFO] Step N: swapped positions i and j` and `[DEBUG] Step N state: [...]` via scribe, sleep `delayBetweenStepsMs` ms between steps, on completion build `SortingResult` and emit `[INFO] Sort complete — N steps | elapsed: X ms | execution: Y ms`; exit code 0 on success

**Checkpoint**: User Story 1 is fully functional and independently testable. All tests green.
`./mill __.test` passes.

---

## Phase 4: User Story 2 — GUI Bar Chart Visualization (Priority: P2)

**Goal**: When `--gui` is passed, open a Swing window centered on screen showing a bar chart
that updates after each step; terminal INFO logs continue concurrently; window closes on ESC.
Headless systems receive a descriptive error and exit code 2.

**Independent Test**: Run with `--gui --number-of-elements 30 --delay-between-steps-ms 80` on a
machine with a display; verify bar chart updates per step and terminal logs still appear; verify
ESC closes the window.

### Implementation for User Story 2

- [x] T019 [US2] Implement `GuiRenderer.scala` in `src/visualizer/gui/GuiRenderer.scala` with: headless pre-flight check via `GraphicsEnvironment.isHeadless()` (print error to stderr, `sys.exit(2)`), `JFrame` created centered on screen, `update(step: SortingStep, config: CliConfig)` method dispatching `SwingUtilities.invokeLater` to repaint, ESC `KeyStroke` binding to `dispose()` the frame
- [x] T020 [US2] Implement `BarChartPanel.scala` in `src/visualizer/gui/BarChartPanel.scala` as a `JPanel` subclass with `paintComponent` rendering one vertical bar per element (height proportional to `element / maxElementSize`), bars for `movedPositions` highlighted in a distinct color, bar width derived from `panel.width / numberOfElements`
- [x] T021 [US2] Update `Main.scala` in `src/visualizer/Main.scala` to: call `GuiRenderer.assertDisplayAvailable()` before any sort logic when `config.gui` is true, instantiate `GuiRenderer` with `BarChartPanel`, and call `guiRenderer.update(step, config)` after each step's log and delay

**Checkpoint**: User Stories 1 and 2 both work independently. All tests green.

---

## Phase 5: User Story 3 — Timing and Performance Reporting (Priority: P3)

**Goal**: Verify that the timing formula in `SortingResult` (`executionMs = elapsedMs − delay ×
steps`) is accurate within the ±5 ms tolerance defined by SC-002, and that both timing metrics
appear in the completion INFO log.

**Independent Test**: Run with `--delay-between-steps-ms 50` and note step count N; verify
`elapsedMs >= 50 × N` and `executionMs = elapsedMs − (50 × N)` within ±5 ms. Run with
`--delay-between-steps-ms 0`; verify `|elapsedMs − executionMs| < 5 ms`.

### Tests for User Story 3

- [x] T022 [US3] Add timing accuracy scenario to `BubbleSortSuite.scala` in `test/src/visualizer/domain/algorithms/BubbleSortSuite.scala`: given a known `delayBetweenStepsMs` and the emitted step count, assert `SortingResult.executionMs == elapsedMs - (delayBetweenStepsMs * stepCount)` using ScalaCheck property on arbitrary step counts and delays

**Checkpoint**: Timing formula is verified to pass. All tests green.

---

## Phase 6: User Story 4 — Project Documentation (Priority: P4)

**Goal**: All three documentation files exist, contain no placeholder text, and fully cover
their assigned scope.

**Independent Test**: Verify `README.md`, `CONTRIBUTING.md`, and `CLAUDE.md` exist, cover their
required scope, and a developer unfamiliar with the project can build and run the tool using only
`CONTRIBUTING.md`.

- [x] T023 [P] [US4] Write `README.md` at repo root covering: what the tool does, all CLI flags and their defaults (from `contracts/cli-schema.md`), and two example invocations from `quickstart.md` (bubble sort, quick sort with GUI)
- [x] T024 [P] [US4] Write `CONTRIBUTING.md` at repo root covering: prerequisites (JDK 17+, Mill bootstrap), clone-and-verify steps, TDD workflow (fail → implement → green → format → lint → commit), all Mill commands from `quickstart.md`, and branch/PR guidelines
- [x] T025 [US4] Update `CLAUDE.md` at repo root to reflect the final module layout, key design decisions (functional core / imperative shell, accepted INFO-per-step deviation), and Mill commands

**Checkpoint**: All three documentation files present and complete. SC-004 and SC-005 satisfied.

---

## Final Phase: Polish & Cross-Cutting Concerns

**Purpose**: Format, lint, full test run, and end-to-end validation.

- [x] T026 [P] Run `./mill mill.scalalib.scalafmt/` to format all source files with scalafmt; fix any formatting issues
- [x] T027 [P] Run `./mill __.fix` to apply scalafix lint rules; resolve any reported violations
- [x] T028 Run `./mill __.test` to confirm all tests pass after formatting and linting (depends on T026, T027)
- [x] T029 Run `./mill visualizer.run` with the two quickstart.md sample invocations (bubble sort no GUI, quick sort with GUI) to validate end-to-end behavior matches spec acceptance scenarios

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies — start immediately
- **Foundational (Phase 2)**: Depends on Phase 1 completion — BLOCKS all user stories
- **User Stories (Phases 3–6)**: All depend on Phase 2 completion; P1→P2→P3→P4 priority order
  - US3 (Phase 5) also depends on US1 (Phase 3) — timing test needs algorithms running
  - US2 (Phase 4) depends on US1 (Phase 3) — Main.scala update builds on T018
- **Polish (Final Phase)**: Depends on all user stories being complete

### User Story Dependencies

| Story | Depends on | Can parallelize after |
|-------|------------|----------------------|
| US1 (P1) | Phase 2 complete | T013 done |
| US2 (P2) | US1 (T018) complete | T018 done |
| US3 (P3) | US1 (T018) complete | T018 done |
| US4 (P4) | US1 + US2 (T021) complete | T021 done |

### Within Each User Story

1. Tests MUST be written and FAIL before implementation (TDD)
2. Domain models before algorithms/services
3. Algorithms before Main.scala wiring
4. Core implementation before integration

### Parallel Opportunities

- T003, T004 can run in parallel with T001, T002 (different files)
- T005, T006, T007, T008, T009 can all run in parallel (independent domain models)
- T010, T012 can run in parallel (different test files, different dependencies)
- T011, T013 can run in parallel after their respective tests (T010→T011, T012→T013)
- T014, T015 can run in parallel (different algorithm test files)
- T016, T017 can run in parallel after their respective tests (T014→T016, T015→T017)
- T023, T024 can run in parallel (different documentation files)
- T026, T027 can run in parallel (format and lint are independent)

---

## Parallel Example: Phase 2 Foundation

```
# Launch all domain models together (5 parallel tasks):
T005: Algorithm.scala
T006: DataStructure.scala
T007: SortableCollection.scala
T008: SortingStep.scala
T009: SortingResult.scala

# Once T005+T006 done, launch in parallel:
T010: CliParserSuite.scala (tests first)
T012: CollectionGeneratorSuite.scala (tests first — needs T007)

# Once T010 done:
T011: CliParser.scala (makes T010 pass)

# Once T012 done:
T013: CollectionGenerator.scala (makes T012 pass)
```

## Parallel Example: User Story 1

```
# Launch algorithm tests in parallel (needs T008 done):
T014: BubbleSortSuite.scala
T015: QuickSortSuite.scala

# Once T014 done:
T016: BubbleSort.scala (makes T014 pass)

# Once T015 done:
T017: QuickSort.scala (makes T015 pass)

# Once T016 + T017 + T011 + T013 done:
T018: Main.scala (wires everything together)
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL — blocks all stories)
3. Complete Phase 3: User Story 1
4. **STOP and VALIDATE**: `./mill __.test` green; run quickstart.md bubble-sort invocation
5. Demo CLI sort with INFO logging — MVP delivered

### Incremental Delivery

1. Phase 1 + Phase 2 → Build scaffold ready
2. Phase 3 (US1) → CLI sort with logging → **MVP** ✅
3. Phase 4 (US2) → Add GUI bar chart → Demo with `--gui`
4. Phase 5 (US3) → Verify timing accuracy → SC-002 satisfied
5. Phase 6 (US4) → Complete documentation → SC-004, SC-005 satisfied
6. Final Phase → Polish, format, lint, end-to-end test

---

## Notes

- **[P]** tasks operate on different files with no incomplete shared dependencies
- **[Story]** label maps each task to a specific user story for traceability
- Each user story is independently completable and testable per its checkpoint
- TDD is mandatory: tests MUST be written and confirmed failing before production code
- Commit after each logical group (e.g., after a failing test + its passing implementation)
- Stop at any checkpoint to validate the story independently before proceeding
- File size limit: 400 lines (source), 800 lines (tests) — per CLAUDE.md
