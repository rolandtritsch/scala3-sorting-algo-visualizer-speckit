# Quickstart: 001-sorting-visualizer

**Branch**: `001-sorting-visualizer` | **Date**: 2026-03-02

## Prerequisites

- JDK 17+ (JDK 21 LTS recommended; any JDK that supports Scala 3.3.x works)
- No global Mill install required — the repo ships a `./mill` bootstrap script (see below)
- `scalafmt` and `scalafix` are managed by Mill plugins — no separate install needed

---

## Install Mill (one-time, per machine)

The repo ships a `./mill` bootstrap script committed at the repo root. It reads the target Mill
version from `.mill-version` and downloads the correct release automatically on first run.

The script comes from the [official Mill repository][mill-repo] (maintained by Li Haoyi,
`com-lihaoyi/mill`). To bootstrap it via curl (run once from the repo root after cloning):

```bash
curl -L https://github.com/com-lihaoyi/mill/raw/refs/heads/main/mill > mill && chmod +x mill
```

The `mill` script and `.mill-version` are committed to the repo, so contributors only need to run
this curl command once on a fresh machine. All subsequent invocations use `./mill` directly.

[mill-repo]: https://github.com/com-lihaoyi/mill

---

## Clone and Verify

```bash
git clone <repo-url>
cd scala3-sorting-algo-visualizer-speckit
git checkout 001-sorting-visualizer

# First time on a new machine: install the mill wrapper (see above)
# curl -L https://github.com/com-lihaoyi/mill/raw/refs/heads/main/mill > mill && chmod +x mill

# Run all tests
./mill __.test
```

All tests must be green before any new code is written.

---

## Run the Tool

```bash
# Compile and run with Mill
./mill visualizer.run \
  --number-of-elements 20 \
  --max-element-size 100 \
  --delay-between-steps-ms 100 \
  --algorithm bubble-sort \
  --data-structure array

# With GUI
./mill visualizer.run \
  --number-of-elements 30 \
  --max-element-size 50 \
  --delay-between-steps-ms 80 \
  --algorithm quick-sort \
  --data-structure vector \
  --gui
```

---

## Source Layout

```
src/
  visualizer/
    Main.scala                # Entry point; wires CLI → domain → logging + GUI
    cli/
      CliParser.scala         # decline-based flag definitions and enum Argument instances
    domain/
      model/
        Algorithm.scala       # Algorithm enum
        DataStructure.scala   # DataStructure enum
        SortableCollection.scala
        SortingStep.scala
        SortingResult.scala
      algorithms/
        BubbleSort.scala      # Pure step-emitting bubble sort
        QuickSort.scala       # Pure step-emitting quick sort
      generator/
        CollectionGenerator.scala  # Random collection generation
    gui/
      GuiRenderer.scala       # JFrame lifecycle, headless check
      BarChartPanel.scala     # JPanel subclass, paintComponent

test/src/
  visualizer/
    cli/
      CliParserSuite.scala
    domain/
      algorithms/
        BubbleSortSuite.scala
        QuickSortSuite.scala
      generator/
        CollectionGeneratorSuite.scala
```

---

## TDD Workflow

The constitution mandates test-first. For every new function:

1. Write a **failing unit test** (happy path).
2. Write a **property test** with ScalaCheck for correctness invariants.
3. Write an **edge-case test** (empty input, single element, already-sorted, degenerate input).
4. Run `./mill __.test` — confirm red.
5. Write the minimum production code to make tests green.
6. Run `./mill __.reformatAll` (scalafmt) and `./mill __.fix` (scalafix).
7. Commit.

---

## Key Dependencies

| Library | Version | Purpose |
|---------|---------|---------|
| `com.monovore::decline` | `2.6.0` | CLI argument parsing |
| `com.outr::scribe` | `3.15.0` | Logging (constitution-mandated) |
| `org.scalameta::munit` | (Mill default) | Unit testing |
| `org.scalacheck::scalacheck` | (Mill default) | Property-based testing |
| Swing/AWT | stdlib | GUI rendering (no Maven dep) |

---

## Useful Mill Commands

| Command | Purpose |
|---------|---------|
| `./mill __.test` | Run all tests |
| `./mill visualizer.run <args>` | Run the tool |
| `./mill __.reformatAll` | Format all source files with scalafmt |
| `./mill __.fix` | Run scalafix lint rules |
| `./mill __.assembly` | Build a fat JAR |
