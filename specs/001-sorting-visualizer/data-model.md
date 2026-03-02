# Data Model: 001-sorting-visualizer

**Branch**: `001-sorting-visualizer` | **Date**: 2026-03-02

## Enumerations

### Algorithm

Represents the sorting algorithm to apply.

| Value | CLI token | Description |
|-------|-----------|-------------|
| `BubbleSort` | `bubble-sort` | Repeated adjacent-swap passes |
| `QuickSort` | `quick-sort` | Recursive partition-based sort |

### DataStructure

Represents the in-memory collection type to generate.

| Value | CLI token | Description |
|-------|-----------|-------------|
| `List` | `list` | Immutable linked list |
| `Array` | `array` | Mutable indexed array |
| `Vector` | `vector` | Persistent indexed sequence |

---

## Core Domain Entities

### CliConfig

Parsed, validated representation of all command-line inputs. Lives at the shell layer;
constructed by the CLI parser before any domain logic runs.

| Field | Type | Constraint |
|-------|------|------------|
| `numberOfElements` | `Int` | ≥ 0 |
| `maxElementSize` | `Int` | ≥ 1 |
| `delayBetweenStepsMs` | `Int` | ≥ 0; default: 0 |
| `algorithm` | `Algorithm` | One of the `Algorithm` enum values |
| `dataStructure` | `DataStructure` | One of the `DataStructure` enum values |
| `gui` | `Boolean` | `true` when `--gui` flag is present |

**Validation rules**:

- All fields are validated by the CLI parser before `CliConfig` is constructed.
- If any validation fails the parser returns `Left(Help)` and the main entry point exits with
  code `1`.

---

### SortableCollection

The in-memory collection of integer elements being sorted. Tracks current element order. Treated
as a value in the domain layer — every mutation yields a new snapshot rather than modifying
in place.

| Field | Type | Constraint |
|-------|------|------------|
| `elements` | `IndexedSeq[Int]` | Length = `numberOfElements`; each value in `[1, maxElementSize]` |
| `dataStructure` | `DataStructure` | The structural type used at creation |

**Invariants**:

- `elements.length == numberOfElements` after generation.
- Each element satisfies `1 <= e <= maxElementSize`.
- May be empty (`elements.isEmpty` when `numberOfElements == 0`).

**State transitions**:

```
Generated (unsorted)
      │  (first step applied)
      ▼
Partially sorted  ──(next step)──▶  Partially sorted  ──…──▶  Fully sorted
```

A `SortableCollection` is considered fully sorted when `elements == elements.sorted`.

---

### SortingStep

A single observable unit of sorting work — one swap or element placement. Produced by the
algorithm and consumed by logging and rendering.

| Field | Type | Constraint |
|-------|------|------------|
| `stepIndex` | `Int` | ≥ 1; monotonically increasing |
| `movedPositions` | `(Int, Int)` | Zero-based indices of the two positions involved; `first != second` |
| `collectionAfter` | `IndexedSeq[Int]` | The full collection state immediately after this step |
| `timestamp` | `Long` | Wall-clock ms at the moment the step completed (used for elapsed-time calculation) |

**Invariants**:

- `movedPositions._1` and `movedPositions._2` are valid indices into `collectionAfter`.
- `collectionAfter.length` equals the original `numberOfElements`.
- `stepIndex` starts at 1 and increments by exactly 1 per emitted step.

---

### SortingResult

Produced once when the sort run completes. Contains all timing data required for the FR-007
summary log.

| Field | Type | Constraint |
|-------|------|------------|
| `stepCount` | `Int` | ≥ 0; equals the count of emitted `SortingStep` values |
| `elapsedMs` | `Long` | Total wall-clock time from sort start to sort end, in ms |
| `executionMs` | `Long` | `elapsedMs - (delayBetweenStepsMs × stepCount)` |
| `sortedCollection` | `IndexedSeq[Int]` | Final sorted state |

**Derived field**:

```
executionMs = elapsedMs - (config.delayBetweenStepsMs * stepCount)
```

`executionMs` may be slightly negative under extreme timer jitter; this is acceptable within the
±5 ms tolerance defined in SC-002.

---

### BarChartFrame

A rendering-ready snapshot of the collection, derived from a `SortingStep`. Lives in the GUI
shell layer; never enters the domain layer.

| Field | Type | Description |
|-------|------|-------------|
| `bars` | `IndexedSeq[Double]` | Normalized bar heights in `[0.0, 1.0]`; `bars(i) = elements(i) / maxElementSize.toDouble` |
| `highlightedPositions` | `(Int, Int)` | The positions from `SortingStep.movedPositions`; GUI may color these differently |
| `stepIndex` | `Int` | Passed through from `SortingStep` for optional display |

**Derivation from SortingStep**:

```
bars(i) = step.collectionAfter(i).toDouble / config.maxElementSize
```

---

## Entity Relationships

```
CliConfig ──generates──▶ SortableCollection
                               │
                               │ sorted by Algorithm
                               ▼
               Stream of SortingStep (0..N steps)
                               │
               ┌───────────────┴───────────────┐
               ▼                               ▼
        Logging (INFO + DEBUG)         BarChartFrame (GUI only)
               │                               │
               └───────────────┬───────────────┘
                               ▼
                        SortingResult
```

---

## Log Message Formats

### INFO per step (FR-004)

```
[INFO] Step {stepIndex}: swapped positions {pos1} and {pos2}
```

Example: `[INFO] Step 42: swapped positions 3 and 7`

### DEBUG per step (FR-005)

```
[DEBUG] Step {stepIndex} state: [{e0}, {e1}, ..., {eN}]
```

Example: `[DEBUG] Step 42 state: [1, 3, 5, 2, 8, 4]`

### INFO completion summary (FR-007)

```
[INFO] Sort complete — {stepCount} steps | elapsed: {elapsedMs} ms | execution: {executionMs} ms
```

Example: `[INFO] Sort complete — 120 steps | elapsed: 6150 ms | execution: 150 ms`
