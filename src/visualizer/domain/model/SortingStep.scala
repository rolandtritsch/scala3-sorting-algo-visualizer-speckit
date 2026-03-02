package visualizer.domain.model

/** One observable unit of sorting work — one swap or element placement.
  *
  * @param stepIndex
  *   1-based, monotonically increasing
  * @param movedPositions
  *   zero-based indices of the two positions involved; first != second
  * @param collectionAfter
  *   full collection state immediately after this step
  * @param timestamp
  *   wall-clock ms at the moment the step completed
  */
final case class SortingStep(
    stepIndex: Int,
    movedPositions: (Int, Int),
    collectionAfter: IndexedSeq[Int],
    timestamp: Long
)
