# Research: 001-sorting-visualizer

**Branch**: `001-sorting-visualizer` | **Date**: 2026-03-02

## CLI Argument Parsing Library

### Decision: decline 2.6.0

**Rationale**:

- Most actively maintained candidate — release cut February 2026; commits visible in early 2026.
- `Command[A].parse(args)` returns `Either[Help, A]` — aligns directly with the constitution's
  "errors are values" principle; no `System.exit` call happens inside the library.
- Enum support via `given Argument[T]` typeclass requires ~5 lines per enum and produces
  user-friendly error messages natively.
- Single-command CLI is decline's design sweet spot; no subcommand boilerplate to work around.
- `cats-core` transitive dependency is a utility library (not an effect system); `Either` and
  `Validated` usage is explicitly permitted at the shell layer by the constitution.

**Alternatives considered**:

- **mainargs 0.7.8**: No native Scala 3 enum support (GitHub issue #200, open as of early 2026);
  `runOrExit` conflicts with "errors are values" — must use `runEither` workaround; enum
  validation must live in the function body rather than the parser.
- **scopt 4.1.0**: Mutable builder DSL; slower release cadence (last stable July 2024); no native
  Scala 3 enum derivation; the `OParser` `.copy()` pattern is the anti-pattern the constitution
  avoids in domain code (acceptable at shell layer but adds cognitive overhead).
- **case-app 2.1.0**: Last stable August 2024; sparse documentation; no first-class enum path;
  error handling requires wrapping.

**Mill dependency**:

```scala
ivy"com.monovore::decline:2.6.0"
```

**Enum wiring pattern** (5 lines per enum):

```scala
given Argument[Algorithm] with
  def read(s: String) =
    Algorithm.values.find(_.entryName == s)
      .toValidNel(s"Unknown algorithm '$s'. Valid values: ${Algorithm.values.map(_.entryName).mkString(", ")}")
  def defaultMetavar = "algorithm"
```

---

## GUI Rendering Library

### Decision: Java Swing/AWT (stdlib — no added dependency)

**Rationale**:

- Zero additional Maven dependencies — `java.desktop` module ships in every OpenJDK distribution
  on Linux, macOS, and Windows.
- `GraphicsEnvironment.isHeadless()` is a documented JDK contract — the most reliable
  cross-platform headless pre-flight check available (FR-010). No env-var sniffing required.
- JDK 8+ compatible — imposes no JVM floor beyond what Scala 3 already requires; CI using any
  modern JDK works without extra configuration.
- The entire GUI surface (one panel, one custom `paintComponent`, one key binding, one
  `invokeLater` per step) fits in a single `GuiRenderer` source file well under the 400-line
  limit.
- The "functional core" principle is fully preserved: domain layer (step generation, model,
  frame construction) is pure; Swing mutation is confined to the application-shell `GuiRenderer`
  — exactly the boundary the constitution permits.

**Alternatives considered**:

- **ScalaFX 25.0.2-R37**: Requires 10 Maven coordinates with platform-specific native JARs,
  JDK 23+ minimum floor (breaks teams on JDK 21 LTS and any CI not pinned to JDK 23+), and
  headless detection via environment variables (fragile). Significant complexity for a 60–80 line
  rendering surface.
- **Lanterna 3.1.3**: Terminal TUI library; character-grid resolution is incompatible with the
  spec's proportional bar chart requirement at the 500-element scale target. Disqualified.

**Mill dependency**: None (stdlib)

---

## Headless Detection Pattern

The following check MUST be the first action taken when `--gui` is detected, before any
Swing/AWT object is constructed:

```scala
import java.awt.GraphicsEnvironment

def assertDisplayAvailable(): Unit =
  if GraphicsEnvironment.isHeadless() then
    System.err.println(
      "Error: no display available — cannot open GUI window. " +
      "Run the tool without --gui on a headless system."
    )
    sys.exit(2)
```

Exit code `2` is reserved for the no-display error to distinguish it from parse errors (exit `1`).

---

## Swing Threading Model

Swing enforces a single-threaded Event Dispatch Thread (EDT). Sort steps run on the main thread
(or a dedicated sorting thread); each GUI update must be dispatched via:

```scala
javax.swing.SwingUtilities.invokeLater(() => panel.repaint())
```

The domain layer emits `SortingStep` values; the `GuiRenderer` subscribes and dispatches repaints.
No mutable state crosses the domain boundary.

---

## Scribe Logging Configuration

Scribe (the constitution-mandated logging library) defaults to INFO level. To surface DEBUG logs
the caller sets `scribe.Logger.root.withMinimumLevel(scribe.Level.Debug)` at startup. This
replaces any need for a `--log-level` flag (not in spec; out of scope).

**Mill dependency**:

```scala
ivy"com.outr::scribe:3.15.0"
```
