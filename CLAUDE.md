# scala3-sorting-algo-visualizer-speckit Development Guidelines

Auto-generated from all feature plans. Last updated: 2026-03-02

## Active Technologies

- Scala 3 (latest stable LTS — 3.3.x) + scribe 3.15.0 (logging), decline 2.6.0 (CLI parsing), Swing/AWT (001-sorting-visualizer)

## Project Structure

```text
src/
  visualizer/
    Main.scala                      # Entry point
    cli/CliParser.scala             # decline CLI parser
    domain/model/                   # Algorithm, DataStructure, SortableCollection, SortingStep, SortingResult
    domain/algorithms/              # BubbleSort, QuickSort (pure step emitters)
    domain/generator/               # CollectionGenerator
    gui/GuiRenderer.scala           # JFrame lifecycle, headless check
    gui/BarChartPanel.scala         # Swing paintComponent bar chart

test/src/
  visualizer/
    cli/CliParserSuite.scala
    domain/algorithms/              # BubbleSortSuite, QuickSortSuite
    domain/generator/               # CollectionGeneratorSuite
```

## Commands

| Command | Purpose |
|---------|---------|
| `./mill __.test` | Run all tests (required before every commit) |
| `./mill visualizer.run <args>` | Run the tool |
| `./mill mill.scalalib.scalafmt/` | Format all source files with scalafmt |
| `./mill __.fix` | Lint with scalafix (mill-scalafix 0.6.0) |
| `./mill __.assembly` | Build fat JAR |

> **Note**: The scalafmt command changed from `./mill __.reformatAll` (Mill 0.x)
> to `./mill mill.scalalib.scalafmt/` in Mill 1.x. This project targets Mill 1.1.2.

## Code Style

- Scala 3 idioms: enums, opaque types, extension methods, `given`/`using`
- Domain layer: pure functions only; no `var`, no side effects
- Shell layer (cli/, gui/): `Either`/`Option` for error handling; no thrown exceptions crossing domain boundaries
- File size limit: 400 lines (source), 800 lines (tests)
- TDD: failing test before any production code; unit + property + edge-case per function

## Key Design Decisions

- **Functional core / imperative shell**: `domain/` is pure (no `var`, no side effects); `cli/`
  and `gui/` are the shell where Swing mutation and CLI error handling live.
- **INFO per step (FR-004 deviation)**: Step-level logging uses INFO (not DEBUG) per explicit
  user clarification on 2026-03-02. DEBUG is still used for full collection snapshots (FR-005).
- **Mill 1.x source layout override**: `build.mill` uses `Task.Sources(moduleDir / os.up / ...)`
  to map `visualizer/src/ → src/visualizer/` and `visualizer/test/src/ → test/src/visualizer/`.
- **Algorithm step definition**: one swap or element placement per step, consistent across both
  algorithms. BubbleSort emits one step per adjacent swap. QuickSort emits one step per element
  movement during Lomuto partitioning.
- **Timing formula**: `executionMs = elapsedMs − (delayBetweenStepsMs × stepCount)`, computed
  in Main.scala and stored in `SortingResult`.

## Recent Changes

- 001-sorting-visualizer: Implemented full feature — CLI parser (decline 2.6.0), BubbleSort,
  QuickSort, CollectionGenerator, Main.scala pipeline, GuiRenderer + BarChartPanel (Swing/AWT),
  scribe 3.15.0 logging, Mill 1.1.2 build, 137 tests passing.

<!-- MANUAL ADDITIONS START -->
<!-- MANUAL ADDITIONS END -->
