# scala3-sorting-algo-visualizer-speckit - Constitution

## Core Principles

### CLI Contract (NON-NEGOTIABLE)

The tool exposes its functionality exclusively through a stable, documented CLI
interface. Input arrives via positional arguments and named flags; results go to
stdout; errors and diagnostics go to stderr. Exit codes MUST be meaningful: `0`
for success, any non-zero for failure. The tool MUST be pipeable and scriptable
— no interactive prompts are permitted in the core execution path. Output format
decisions (human-readable vs. machine-readable modes) MUST be made at project
init and applied uniformly across all commands.

### Test-First with MUnit (NON-NEGOTIABLE)

TDD is mandatory. No production code may be written before a failing test exists
and has been reviewed. The Red-Green-Refactor cycle is strictly enforced. MUnit
is the canonical test framework. Domain invariants MUST be covered by
property-based tests using ScalaCheck. Every new algorithm or domain function
requires at minimum: a unit test for the happy path, a property test for
correctness invariants, and an edge-case test (empty input, single element,
already-sorted or degenerate input).

### Pragmatic Functional Core

Domain logic — algorithm step generation, state modeling, frame construction —
MUST live in pure, side-effect-free functions. Mutable state is prohibited in
the domain layer; algorithms produce immutable step sequences. At the
application shell (CLI argument parsing, rendering, file I/O) pragmatic Scala is
acceptable: `Option`, `Either`, and simple `try/catch` blocks are preferred over
introducing a full effect system. `null` and thrown exceptions MUST NOT cross
domain boundaries — errors are values.

### Observability Through Logging

All production code paths MUST be instrumented with log statements at the
appropriate level. The five levels MUST be used consistently: `error` for
unrecoverable failures; `warn` for recoverable anomalies; `info` for key
lifecycle events; `debug` for developer-facing state transitions; `trace` for
fine-grained, step-by-step detail. Silent failure is prohibited — every
exceptional condition MUST produce at least an `error` or `warn` log entry.
scribe is the canonical logging library; it is the only logging dependency
permitted.

### Simplicity and Restraint

YAGNI applies at every level. No abstraction is introduced for fewer than three
concrete use cases.  Source files MUST NOT exceed 400 lines; test files MUST NOT
exceed 800 lines. Scala 3 language features (opaque types, enums, extension
methods, `given`/`using`) are preferred over boilerplate patterns, but
type-level complexity requires explicit justification. All features follow the
speckit workflow: `specify` → `clarify` → `plan` → `tasks` →
`implement`. Complexity introduced by any PR must be justified in the PR
description.

## Technology Stack

- **Language**: Scala 3 (latest stable LTS)
- **Build tool**: Mill — `./mill __.test` is the only required local
  verification command
- **Testing**: MUnit with ScalaCheck for property-based tests
- **Formatter**: scalafmt — all source files must be formatted before merge;
  formatting is non-negotiable and non-configurable beyond the project's
  `.scalafmt.conf`
- **Linter**: scalafix — lint rules must pass before merge; rule additions
  require maintainer approval and a migration note
- **Logging**: scribe — pure Scala 3, zero-config, applied consistently across
  all code paths
- **CLI parsing**: One library, consistently applied across all commands
  (establish at project init)

## Governance

This constitution supersedes all other conventions, README guidance, and prior
informal agreements.  Amendments require a written rationale, explicit
maintainer approval, and a migration note for any in-progress features or open
tasks. All PRs and speckit-generated plans MUST verify compliance with the
current principles before proceeding — the `speckit.plan` constitution check is
non-negotiable.  Version changes follow semantic versioning: MAJOR for principle
removals or redefinitions, MINOR for new principles or sections, PATCH for
clarifications and wording fixes.

**Version**: 1.2.1 | **Ratified**: 2026-03-02 | **Last Amended**: 2026-03-02
