# CLI Contract: visualizer

**Branch**: `001-sorting-visualizer` | **Date**: 2026-03-02

This document is the canonical CLI interface contract for the `visualizer` tool. Any change to
flags, types, valid values, or exit codes is a breaking change and requires a spec update.

---

## Invocation

```
visualizer [OPTIONS]
```

All parameters are named flags. There are no positional arguments.

---

## Flags

| Flag | Type | Required | Default | Valid Values | Description |
|------|------|----------|---------|--------------|-------------|
| `--number-of-elements` | `Int` | yes | — | `>= 0` | Number of elements to generate. `0` produces an empty collection. |
| `--max-element-size` | `Int` | yes | — | `>= 1` | Maximum value of any generated element. Elements are in `[1, max-element-size]`. |
| `--delay-between-steps-ms` | `Int` | no | `0` | `>= 0` | Milliseconds to sleep between sorting steps. |
| `--algorithm` | `Algorithm` | yes | — | `quick-sort`, `bubble-sort` | Sorting algorithm to apply. |
| `--data-structure` | `DataStructure` | yes | — | `list`, `array`, `vector` | In-memory collection type. |
| `--gui` | flag | no | absent | (presence = `true`) | When present, opens a bar chart window. |

---

## Exit Codes

| Code | Meaning | Triggered by |
|------|---------|--------------|
| `0` | Success | Sort completed normally |
| `1` | Invalid arguments | Missing required flag, out-of-range value, unrecognized enum token |
| `2` | No display available | `--gui` passed but `GraphicsEnvironment.isHeadless()` returns `true` |

---

## Output Channels

| Channel | Content |
|---------|---------|
| `stdout` | All log output (INFO and DEBUG) |
| `stderr` | Argument-parse errors (decline library help text); display-unavailable error message |

---

## Log Output Contract

The tool emits structured log lines to `stdout` at two levels:

**INFO** — always visible (default log level):

```
[INFO]  Step {N}: swapped positions {i} and {j}
[INFO]  Sort complete — {stepCount} steps | elapsed: {ms} ms | execution: {ms} ms
```

**DEBUG** — only visible when debug logging is enabled:

```
[DEBUG] Step {N} state: [{e0}, {e1}, ..., {eN-1}]
```

---

## Example Invocations

Sort 20 elements with bubble sort, 150 ms delay between steps:

```bash
visualizer \
  --number-of-elements 20 \
  --max-element-size 100 \
  --delay-between-steps-ms 150 \
  --algorithm bubble-sort \
  --data-structure array
```

Sort 50 elements with quick sort and open the GUI:

```bash
visualizer \
  --number-of-elements 50 \
  --max-element-size 200 \
  --delay-between-steps-ms 50 \
  --algorithm quick-sort \
  --data-structure vector \
  --gui
```

Empty collection (trivially sorted, 0 steps):

```bash
visualizer \
  --number-of-elements 0 \
  --max-element-size 10 \
  --algorithm bubble-sort \
  --data-structure list
```

---

## Error Output Examples

Missing required flag:

```
Error: Missing expected flag (value)

  --number-of-elements <number-of-elements>
    Number of elements to generate (>= 0)

Usage: visualizer --number-of-elements <int> ...
```

Invalid enum value:

```
Error: Invalid value for --algorithm: 'merge-sort'
Valid values: quick-sort, bubble-sort
```

No display available:

```
Error: no display available — cannot open GUI window. Run the tool without --gui on a headless system.
```
