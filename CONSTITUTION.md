# scala3-fib-speckit Constitution

## Core Principles

### I. CLI Contract (NON-NEGOTIABLE)

The tool exposes its functionality exclusively through a stable, documented CLI interface. Input arrives
via positional arguments and named flags; results go to stdout; errors and diagnostics go to stderr. Every
command MUST support both a human-readable default output mode and a `--json` machine-readable mode. Exit
codes MUST be meaningful: `0` for success, any non-zero for failure. The tool MUST be pipeable and
scriptable — no interactive prompts are permitted in the core execution path.

### II. Visualizer-First Design

Sorting algorithms are represented as lazy sequences of discrete, immutable state snapshots — one per
comparison or swap — rather than as procedures that produce a final result. Each snapshot MUST carry
enough information to render a complete frame independently of any other snapshot. Rendering logic MUST
be decoupled from algorithm logic; an algorithm emits steps, a renderer consumes them. This separation
MUST be preserved across all algorithms and output formats (ASCII, JSON, future targets).

### III. Test-First with MUnit (NON-NEGOTIABLE)

TDD is mandatory. No production code may be written before a failing test exists and has been reviewed.
The Red-Green-Refactor cycle is strictly enforced. MUnit is the canonical test framework. Sorting
correctness properties (output is sorted, length is preserved, all original elements are present) MUST
be covered by property-based tests using ScalaCheck. Every new algorithm requires at minimum: a unit
test for the happy path, a property test for correctness, and an edge-case test (empty input, single
element, already-sorted input).

### IV. Pragmatic Functional Core

Domain logic — algorithm step generation, state modeling, frame construction — MUST live in pure,
side-effect-free functions. Mutable state is prohibited in the domain layer; algorithms produce
immutable step sequences. At the application shell (CLI argument parsing, rendering, file I/O) pragmatic
Scala is acceptable: `Option`, `Either`, and simple `try/catch` blocks are preferred over introducing a
full effect system. `null` and thrown exceptions MUST NOT cross domain boundaries — errors are values.

### V. Simplicity and Restraint

YAGNI applies at every level. No abstraction is introduced for fewer than three concrete use cases.
Source files MUST NOT exceed 400 lines; test files MUST NOT exceed 800 lines. Scala 3 language features
(opaque types, enums, extension methods, `given`/`using`) are preferred over boilerplate patterns, but
type-level complexity requires explicit justification. All features follow the speckit workflow:
`specify` → `clarify` → `plan` → `tasks` → `implement`. Complexity introduced by any PR must be
justified in the PR description.

## Technology Stack

- **Language**: Scala 3 (latest stable LTS)
- **Build tool**: Mill — `./mill __.test` is the only required local verification command
- **Testing**: MUnit with ScalaCheck for property-based tests
- **CLI parsing**: One library, consistently applied across all commands (establish at project init)
- **Output formats**: Plain text (default) and JSON (`--json` flag) for all commands

## Governance

This constitution supersedes all other conventions, README guidance, and prior informal agreements.
Amendments require a written rationale, explicit maintainer approval, and a migration note for any
in-progress features or open tasks. All PRs and speckit-generated plans MUST verify compliance with
the current principles before proceeding — the `speckit.plan` constitution check is non-negotiable.
Version changes follow semantic versioning: MAJOR for principle removals or redefinitions, MINOR for
new principles or sections, PATCH for clarifications and wording fixes.

**Version**: 1.0.0 | **Ratified**: 2026-03-02 | **Last Amended**: 2026-03-02
