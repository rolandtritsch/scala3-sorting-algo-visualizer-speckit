# Sorting Algorithm Visualizer

A Scala 3 CLI tool that generates a random collection of integers, sorts it
step-by-step using a chosen algorithm, and emits one INFO log line per swap
so you can observe sorting progress. An optional `--gui` flag opens a Swing
bar-chart window that updates in real time.

## What it does

1. Generates `--number-of-elements` random integers in `[1, --max-element-size]`.
2. Sorts them with the chosen algorithm, pausing `--delay-between-steps-ms` ms
   between each swap.
3. Logs every swap at INFO level and the full collection state at DEBUG level.
4. Prints elapsed time and net execution time (elapsed minus accumulated delay)
   on completion.
5. Optionally renders the sort as a live bar chart (`--gui`).

## CLI flags

| Flag | Required | Default | Description |
|------|----------|---------|-------------|
| `--number-of-elements` | yes | — | Elements to generate (`>= 0`). `0` = empty collection. |
| `--max-element-size` | yes | — | Max element value (`>= 1`). Elements drawn from `[1, max]`. |
| `--delay-between-steps-ms` | no | `0` | Milliseconds to sleep between swaps. |
| `--algorithm` | yes | — | `bubble-sort` or `quick-sort` |
| `--data-structure` | yes | — | `list`, `array`, or `vector` |
| `--gui` | no | absent | Open a Swing bar-chart window. Requires a display. |

## Exit codes

| Code | Meaning |
|------|---------|
| `0` | Success |
| `1` | Invalid or missing argument |
| `2` | `--gui` passed but no display available |

## Example invocations

Sort 20 elements with bubble sort, 150 ms delay, no GUI:

```bash
./mill visualizer.run \
  --number-of-elements 20 \
  --max-element-size 100 \
  --delay-between-steps-ms 150 \
  --algorithm bubble-sort \
  --data-structure array
```

Sort 30 elements with quick sort and open the bar-chart GUI:

```bash
./mill visualizer.run \
  --number-of-elements 30 \
  --max-element-size 50 \
  --delay-between-steps-ms 80 \
  --algorithm quick-sort \
  --data-structure vector \
  --gui
```

Empty collection (0 steps, exits immediately):

```bash
./mill visualizer.run \
  --number-of-elements 0 \
  --max-element-size 10 \
  --algorithm bubble-sort \
  --data-structure list
```

## Log output format

```
[INFO]  Step 42: swapped positions 3 and 7
[DEBUG] Step 42 state: [1, 3, 5, 2, 8, 4]
[INFO]  Sort complete — 120 steps | elapsed: 6150 ms | execution: 150 ms
```

DEBUG lines are hidden by default. To enable them, add a scribe configuration
before the sort starts (see [CLAUDE.md] for details).

## See also

- [CLAUDE.md] — code architecture, module layout, and contribution guidance
- [CONTRIBUTING.md] — how to build, test, and submit changes

[CLAUDE.md]: CLAUDE.md
[CONTRIBUTING.md]: CONTRIBUTING.md
