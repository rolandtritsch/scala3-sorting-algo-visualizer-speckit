# Implementation Plan: Sorting Algorithm Visualizer CLI

**Branch**: `001-sorting-visualizer` | **Date**: 2026-03-02 | **Spec**: [spec.md](spec.md)
**Input**: Feature specification from `specs/001-sorting-visualizer/spec.md`

## Summary

Build a Scala 3 CLI tool (`visualizer`) that generates a random collection of integers, sorts it
using a user-selected algorithm (bubble sort or quick sort), emits one INFO log per step plus a
DEBUG snapshot, sleeps a configurable delay between steps, and reports elapsed and net execution
times on completion. An optional `--gui` flag opens a Swing bar chart window that updates after
every step and closes on ESC. All domain logic is pure; the CLI parser (decline) and GUI renderer
(Swing) are the application shell.

## Technical Context

**Language/Version**: Scala 3 (latest stable LTS — 3.3.x)
**Build tool**: Mill — invoked as `./mill` via the official bootstrap script from
[com-lihaoyi/mill][] committed to the repo root; installed once per machine with
`curl -L https://github.com/com-lihaoyi/mill/raw/refs/heads/main/mill > mill && chmod +x mill`;
target version pinned in `.mill-version`
**Primary Dependencies**: scribe 3.15.0 (logging), decline 2.6.0 (CLI parsing), Swing/AWT
(stdlib — GUI), MUnit + ScalaCheck (testing), scalafmt (formatter), scalafix (linter)
**Storage**: N/A — in-memory only
**Testing**: MUnit (unit), ScalaCheck (property-based), MUnit integration tests
**Target Platform**: JVM desktop — Linux, macOS, Windows
**Project Type**: CLI tool with optional GUI window
**Performance Goals**: No strict latency target; step delay is user-controlled. GUI must update
within one frame per step at up to 500 elements (SC-003).
**Constraints**: Source files ≤ 400 lines; test files ≤ 800 lines; no effect system; functional
domain core; no mutable state crossing domain boundaries
**Scale/Scope**: Single-user, local, single-process tool

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-checked after Phase 1 design.*

| Principle | Status | Notes |
|-----------|--------|-------|
| CLI Contract | PASS | All input via named flags; stdout for logs; stderr for errors; exit codes 0/1/2 defined; no interactive prompts |
| Test-First with MUnit | PASS | Every algorithm function requires unit + property + edge-case tests before production code |
| Pragmatic Functional Core | PASS | Domain layer (step generation, model, frame construction) is pure. Swing EDT and CLI parsing confined to shell layer |
| Observability Through Logging | PASS | scribe is the only logging dependency; INFO for step events and completion; DEBUG for collection snapshots; ERROR/WARN for failures |
| Simplicity and Restraint | PASS | Two source files max for GUI (GuiRenderer + BarChartPanel); no abstraction for fewer than 3 use cases; YAGNI enforced |
| Technology Stack | PASS | Scala 3, Mill, MUnit + ScalaCheck, scalafmt, scalafix, scribe, decline, Swing stdlib |
| CI/CD | PASS | GitHub Actions CI required on every push (FR-012 / CONTRIBUTING.md covers setup) |

**Accepted deviation**: FR-004 emits one INFO log per sort step (swap-level granularity). The
constitution designates INFO for "key lifecycle events" and DEBUG for "developer-facing state
transitions." User explicitly selected INFO for step progress in clarification Q3 on 2026-03-02.
This is an accepted, documented deviation; DEBUG is still used for full snapshots per FR-005.

**Post-Phase-1 re-check**: No new violations introduced by design. The `BarChartFrame` derivation
and `GuiRenderer` are shell-layer code; the pure domain boundary is preserved.

## Project Structure

### Documentation (this feature)

```text
specs/001-sorting-visualizer/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/
│   └── cli-schema.md    # Phase 1 output (/speckit.plan command)
├── checklists/
│   └── requirements.md  # From /speckit.specify
└── tasks.md             # Phase 2 output (/speckit.tasks command)
```

### Source Code (repository root)

```text
src/
  visualizer/
    Main.scala                      # Entry point; wires CLI → domain → logging + GUI
    cli/
      CliParser.scala               # decline Command[CliConfig] + Argument[Algorithm/DataStructure]
    domain/
      model/
        Algorithm.scala             # enum Algorithm { BubbleSort, QuickSort }
        DataStructure.scala         # enum DataStructure { List, Array, Vector }
        SortableCollection.scala    # value class wrapping IndexedSeq[Int]
        SortingStep.scala           # case class: stepIndex, movedPositions, collectionAfter, timestamp
        SortingResult.scala         # case class: stepCount, elapsedMs, executionMs, sortedCollection
      algorithms/
        BubbleSort.scala            # pure step emitter: (CliConfig) => LazyList[SortingStep]
        QuickSort.scala             # pure step emitter: (CliConfig) => LazyList[SortingStep]
      generator/
        CollectionGenerator.scala   # generates IndexedSeq[Int] from CliConfig
    gui/
      GuiRenderer.scala             # JFrame lifecycle, headless check, step subscription
      BarChartPanel.scala           # JPanel subclass, paintComponent, BarChartFrame derivation

test/src/
  visualizer/
    cli/
      CliParserSuite.scala          # Happy path + invalid flags + enum validation
    domain/
      algorithms/
        BubbleSortSuite.scala       # Unit + property + edge-case
        QuickSortSuite.scala        # Unit + property + edge-case
      generator/
        CollectionGeneratorSuite.scala  # Size, range, and empty-collection properties
```

**Structure Decision**: Single Mill module (`visualizer`). Domain logic is the functional core;
`cli/` and `gui/` are application shell. This separation enforces the constitution's
"functional core / imperative shell" boundary without over-engineering sub-modules.

## Complexity Tracking

No constitution violations to justify. Section intentionally left blank.

---

[com-lihaoyi/mill]: https://github.com/com-lihaoyi/mill
