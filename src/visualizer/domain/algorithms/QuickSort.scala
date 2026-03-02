package visualizer.domain.algorithms

import visualizer.cli.CliConfig
import visualizer.domain.model.{SortableCollection, SortingStep}

/** Pure quick-sort step emitter using in-place Lomuto partition.
  *
  * Each SortingStep represents one element placement/swap during partitioning.
  */
object QuickSort:

  def sort(collection: SortableCollection, config: CliConfig): LazyList[SortingStep] =
    if collection.size <= 1 then LazyList.empty
    else quickSort(collection.elements, low = 0, high = collection.size - 1, stepIdx = 1)._1

  /** Returns (steps, nextStepIdx, sortedElems). */
  private def quickSort(
      elems: IndexedSeq[Int],
      low: Int,
      high: Int,
      stepIdx: Int
  ): (LazyList[SortingStep], Int, IndexedSeq[Int]) =
    if low >= high then (LazyList.empty, stepIdx, elems)
    else
      val (partitionSteps, pivotIdx, afterPartition, nextIdx) =
        partition(elems, low, high, stepIdx)
      val (leftSteps, idx2, afterLeft) =
        quickSort(afterPartition, low, pivotIdx - 1, nextIdx)
      val (rightSteps, idx3, afterRight) =
        quickSort(afterLeft, pivotIdx + 1, high, idx2)
      (partitionSteps #::: leftSteps #::: rightSteps, idx3, afterRight)

  /** Lomuto partition: returns (steps, pivotFinalIdx, resultElems, nextStepIdx). */
  private def partition(
      elems: IndexedSeq[Int],
      low: Int,
      high: Int,
      stepIdx: Int
  ): (LazyList[SortingStep], Int, IndexedSeq[Int], Int) =
    val pivot = elems(high)
    var current = elems
    var i = low - 1
    var idx = stepIdx
    var steps = LazyList.empty[SortingStep]

    var j = low
    while j < high do
      if current(j) <= pivot then
        i += 1
        if i != j then
          current = swap(current, i, j)
          val step = SortingStep(
            stepIndex = idx,
            movedPositions = (i, j),
            collectionAfter = current,
            timestamp = System.currentTimeMillis()
          )
          steps = steps #::: LazyList(step)
          idx += 1
      j += 1

    // Place pivot in its final position
    i += 1
    if i != high then
      current = swap(current, i, high)
      val step = SortingStep(
        stepIndex = idx,
        movedPositions = (i, high),
        collectionAfter = current,
        timestamp = System.currentTimeMillis()
      )
      steps = steps #::: LazyList(step)
      idx += 1

    (steps, i, current, idx)

  private def swap(elems: IndexedSeq[Int], i: Int, j: Int): IndexedSeq[Int] =
    elems.updated(i, elems(j)).updated(j, elems(i))
