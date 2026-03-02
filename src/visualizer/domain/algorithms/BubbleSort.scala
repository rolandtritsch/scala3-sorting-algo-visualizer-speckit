package visualizer.domain.algorithms

import scala.collection.mutable.ArrayBuffer
import visualizer.cli.CliConfig
import visualizer.domain.model.{SortableCollection, SortingStep}

/** Pure bubble-sort step emitter.
  *
  * Runs repeated left-to-right passes; emits one SortingStep per adjacent swap. Stops when a pass
  * completes with no swaps.
  */
object BubbleSort:

  def sort(collection: SortableCollection, config: CliConfig): LazyList[SortingStep] =
    if collection.size <= 1 then LazyList.empty
    else passes(collection.elements, stepIdx = 1)

  /** Repeatedly run passes until a pass produces no swaps. */
  private def passes(elems: IndexedSeq[Int], stepIdx: Int): LazyList[SortingStep] =
    val (passSteps, swapped, nextElems, nextIdx) = runOnePass(elems, stepIdx)
    if !swapped then LazyList.empty
    else passSteps.to(LazyList) #::: passes(nextElems, nextIdx)

  /** One left-to-right scan; returns all swaps found in this pass. */
  private def runOnePass(
      elems: IndexedSeq[Int],
      startStepIdx: Int
  ): (IndexedSeq[SortingStep], Boolean, IndexedSeq[Int], Int) =
    var current = elems
    var stepIdx = startStepIdx
    var swapped = false
    val steps = ArrayBuffer.empty[SortingStep]

    var i = 0
    while i < current.size - 1 do
      if current(i) > current(i + 1) then
        current = swap(current, i, i + 1)
        steps += SortingStep(
          stepIndex = stepIdx,
          movedPositions = (i, i + 1),
          collectionAfter = current,
          timestamp = System.currentTimeMillis()
        )
        stepIdx += 1
        swapped = true
      i += 1

    (steps.toIndexedSeq, swapped, current, stepIdx)

  private def swap(elems: IndexedSeq[Int], i: Int, j: Int): IndexedSeq[Int] =
    elems.updated(i, elems(j)).updated(j, elems(i))
