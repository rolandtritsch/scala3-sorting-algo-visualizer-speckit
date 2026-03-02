package visualizer.domain.model

/** Produced once when the sort run completes.
  *
  * @param stepCount
  *   total number of emitted SortingStep values (>= 0)
  * @param elapsedMs
  *   total wall-clock time from sort start to sort end, in ms
  * @param executionMs
  *   net time: elapsedMs - (delayBetweenStepsMs * stepCount)
  * @param sortedCollection
  *   final sorted state
  */
final case class SortingResult(
    stepCount: Int,
    elapsedMs: Long,
    executionMs: Long,
    sortedCollection: IndexedSeq[Int]
)
