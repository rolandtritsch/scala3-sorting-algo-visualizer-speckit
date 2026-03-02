package visualizer.cli

import cats.data.ValidatedNel
import cats.syntax.all.*
import com.monovore.decline.*
import visualizer.domain.model.{Algorithm, DataStructure}

/** All validated CLI inputs produced by the parser. */
final case class CliConfig(
    numberOfElements: Int,
    maxElementSize: Int,
    delayBetweenStepsMs: Int,
    algorithm: Algorithm,
    dataStructure: DataStructure,
    gui: Boolean
)

object CliParser:

  // ── Argument instances for domain enums ─────────────────────────────────

  private given Argument[Algorithm] with
    def read(s: String): ValidatedNel[String, Algorithm] =
      Algorithm.values
        .find(_.entryName == s)
        .toValidNel(
          s"Unknown algorithm '$s'. Valid values: ${Algorithm.values.map(_.entryName).mkString(", ")}"
        )
    def defaultMetavar = "algorithm"

  private given Argument[DataStructure] with
    def read(s: String): ValidatedNel[String, DataStructure] =
      DataStructure.values
        .find(_.entryName == s)
        .toValidNel(
          s"Unknown data-structure '$s'. Valid values: ${DataStructure.values
              .map(_.entryName)
              .mkString(", ")}"
        )
    def defaultMetavar = "data-structure"

  // ── Flag definitions ─────────────────────────────────────────────────────

  private val numberOfElementsOpt: Opts[Int] =
    Opts
      .option[Int]("number-of-elements", "Number of elements to generate (>= 0)")
      .validate("Must be >= 0")(_ >= 0)

  private val maxElementSizeOpt: Opts[Int] =
    Opts
      .option[Int]("max-element-size", "Maximum element value (>= 1)")
      .validate("Must be >= 1")(_ >= 1)

  private val delayOpt: Opts[Int] =
    Opts
      .option[Int]("delay-between-steps-ms", "Milliseconds to sleep between steps (>= 0)")
      .withDefault(0)
      .validate("Must be >= 0")(_ >= 0)

  private val algorithmOpt: Opts[Algorithm] =
    Opts.option[Algorithm]("algorithm", "Sorting algorithm: quick-sort | bubble-sort")

  private val dataStructureOpt: Opts[DataStructure] =
    Opts.option[DataStructure]("data-structure", "Collection type: list | array | vector")

  private val guiFlag: Opts[Boolean] =
    Opts.flag("gui", "Open a bar-chart GUI window").orFalse

  // ── Command ──────────────────────────────────────────────────────────────

  private val command: Command[CliConfig] =
    Command(name = "visualizer", header = "Sorting algorithm visualizer")(
      (numberOfElementsOpt, maxElementSizeOpt, delayOpt, algorithmOpt, dataStructureOpt, guiFlag)
        .mapN(CliConfig.apply)
    )

  /** Parse args; returns Right(CliConfig) on success or Left(Help) on error. */
  def parse(args: Seq[String]): Either[Help, CliConfig] =
    command.parse(args, sys.env)
