# Contributing

This document explains how to build, test, and run the tool, and how to
submit changes. See [README.md] for what the tool does.

## Prerequisites

- **JDK 17 or later** (JDK 21 LTS recommended)
- No global Mill install required — the repo ships a `./mill` bootstrap script

## Bootstrap Mill (one-time per machine)

The `./mill` wrapper is committed to the repo root. It reads `.mill-version`
and downloads the correct Mill release automatically on first run. On a fresh
machine, ensure the script is executable:

```bash
chmod +x mill
```

If the script is missing, re-download it from the official Mill repository:

```bash
curl -L https://github.com/com-lihaoyi/mill/raw/refs/heads/main/mill > mill \
  && chmod +x mill
```

## Clone and verify

```bash
git clone <repo-url>
cd scala3-sorting-algo-visualizer-speckit
git checkout 001-sorting-visualizer
chmod +x mill          # ensure mill is executable
./mill __.test         # all tests must pass before writing any code
```

## Key Mill commands

| Command | Purpose |
|---------|---------|
| `./mill __.test` | Run all tests (required before every commit) |
| `./mill visualizer.run <args>` | Run the tool |
| `./mill mill.scalalib.scalafmt/` | Format all source files with scalafmt |
| `./mill __.fix` | Lint with scalafix |
| `./mill __.assembly` | Build a fat JAR |

## TDD workflow

The project follows strict TDD. For every new function:

1. Write a **failing unit test** (happy path).
2. Write a **ScalaCheck property test** for correctness invariants.
3. Write an **edge-case test** (empty input, single element, already sorted,
   degenerate input).
4. Run `./mill __.test` — confirm the new tests are RED.
5. Write the minimum production code to make tests GREEN.
6. Run `./mill mill.scalalib.scalafmt/` (format) and `./mill __.fix` (lint).
7. Commit.

## Branch and PR guidelines

- Branch from `001-sorting-visualizer` for all feature work on this feature.
- Main integration branch: `trunk`.
- All tests must pass (`./mill __.test`) before opening a pull request.
- CI runs `./mill __.test` on every push and pull request.
- Keep commits atomic: one logical change per commit.

## Project structure

See [CLAUDE.md] for the full module layout and architecture notes.

[README.md]: README.md
[CLAUDE.md]: CLAUDE.md
