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
| `./mill __.reformatAll` | Format with scalafmt |
| `./mill __.fix` | Lint with scalafix |
| `./mill __.assembly` | Build fat JAR |

## Code Style

- Scala 3 idioms: enums, opaque types, extension methods, `given`/`using`
- Domain layer: pure functions only; no `var`, no side effects
- Shell layer (cli/, gui/): `Either`/`Option` for error handling; no thrown exceptions crossing domain boundaries
- File size limit: 400 lines (source), 800 lines (tests)
- TDD: failing test before any production code; unit + property + edge-case per function

## Recent Changes

- 001-sorting-visualizer: Added Scala 3 (latest stable LTS — 3.3.x) + scribe 3.15.0 (logging), decline 2.6.0 (CLI parsing), Swing/AWT

<!-- MANUAL ADDITIONS START -->
<!-- MANUAL ADDITIONS END -->
