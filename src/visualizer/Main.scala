package visualizer

import visualizer.cli.{CliConfig, CliParser}
import visualizer.domain.algorithms.{BubbleSort, QuickSort}
import visualizer.domain.generator.CollectionGenerator
import visualizer.domain.model.{Algorithm, SortingResult, SortingStep}

object Main:

  def main(args: Array[String]): Unit =
    CliParser.parse(args.toSeq) match
      case Left(help) =>
        System.err.println(help)
        sys.exit(1)
      case Right(config) =>
        run(config)

  private def run(config: CliConfig): Unit =
    // GUI headless check before any sort logic
    if config.gui then gui.GuiRenderer.assertDisplayAvailable()

    val collection = CollectionGenerator.generate(config)

    val guiOpt: Option[gui.GuiRenderer] =
      if config.gui then
        val r = new gui.GuiRenderer(config)
        r.open()
        Some(r)
      else None

    val steps = config.algorithm match
      case Algorithm.BubbleSort => BubbleSort.sort(collection, config)
      case Algorithm.QuickSort => QuickSort.sort(collection, config)

    val startMs = System.currentTimeMillis()
    var stepCount = 0
    var lastCollection = collection.elements

    for step <- steps do
      stepCount += 1
      lastCollection = step.collectionAfter
      logStep(step)
      guiOpt.foreach(_.update(step, config))
      if config.delayBetweenStepsMs > 0 then
        Thread.sleep(config.delayBetweenStepsMs.toLong)

    val elapsedMs = System.currentTimeMillis() - startMs

    val result = SortingResult(
      stepCount = stepCount,
      elapsedMs = elapsedMs,
      executionMs = elapsedMs - (config.delayBetweenStepsMs.toLong * stepCount),
      sortedCollection = lastCollection
    )

    logCompletion(result)
    guiOpt.foreach(_.awaitClose())

  private def logStep(step: SortingStep): Unit =
    val (i, j) = step.movedPositions
    scribe.info(s"Step ${step.stepIndex}: swapped positions $i and $j")
    scribe.debug(
      s"Step ${step.stepIndex} state: [${step.collectionAfter.mkString(", ")}]"
    )

  private def logCompletion(result: SortingResult): Unit =
    scribe.info(
      s"Sort complete — ${result.stepCount} steps | " +
        s"elapsed: ${result.elapsedMs} ms | " +
        s"execution: ${result.executionMs} ms"
    )
