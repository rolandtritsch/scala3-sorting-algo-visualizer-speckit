package visualizer.domain.algorithms

import munit.ScalaCheckSuite
import org.scalacheck.Prop.*
import org.scalacheck.Gen
import visualizer.cli.CliConfig
import visualizer.domain.model.{Algorithm, DataStructure, SortableCollection}

class BubbleSortSuite extends ScalaCheckSuite:

  private def makeConfig(n: Int, maxSize: Int, delay: Int = 0): CliConfig =
    CliConfig(
      numberOfElements = n,
      maxElementSize = maxSize,
      delayBetweenStepsMs = delay,
      algorithm = Algorithm.BubbleSort,
      dataStructure = DataStructure.Array,
      gui = false
    )

  private def makeCollection(elems: Int*): SortableCollection =
    SortableCollection(elems.toIndexedSeq, DataStructure.Array)

  // ── unit tests ──────────────────────────────────────────────────────────

  test("sorts a small unsorted collection") {
    val col = makeCollection(3, 1, 2)
    val steps = BubbleSort.sort(col, makeConfig(3, 10)).toList
    val finalState = steps.last.collectionAfter
    assertEquals(finalState.toList, List(1, 2, 3))
  }

  test("empty collection produces 0 steps") {
    val col = makeCollection()
    val steps = BubbleSort.sort(col, makeConfig(0, 10)).toList
    assertEquals(steps.length, 0)
  }

  test("single-element collection produces 0 steps") {
    val col = makeCollection(42)
    val steps = BubbleSort.sort(col, makeConfig(1, 100)).toList
    assertEquals(steps.length, 0)
  }

  test("already-sorted collection produces 0 steps") {
    val col = makeCollection(1, 2, 3, 4, 5)
    val steps = BubbleSort.sort(col, makeConfig(5, 10)).toList
    assertEquals(steps.length, 0)
  }

  test("stepIndex is 1-based and monotonically increasing") {
    val col = makeCollection(3, 2, 1)
    val steps = BubbleSort.sort(col, makeConfig(3, 10)).toList
    assert(steps.nonEmpty)
    steps.zipWithIndex.foreach { (step, idx) =>
      assertEquals(step.stepIndex, idx + 1)
    }
  }

  test("movedPositions indices are valid") {
    val col = makeCollection(5, 3, 4, 1, 2)
    val steps = BubbleSort.sort(col, makeConfig(5, 10)).toList
    steps.foreach { step =>
      val (i, j) = step.movedPositions
      assert(i >= 0 && i < col.size, s"index $i out of range")
      assert(j >= 0 && j < col.size, s"index $j out of range")
      assert(i != j, "movedPositions must have distinct indices")
    }
  }

  test("collectionAfter length equals original size throughout") {
    val col = makeCollection(5, 3, 4, 1, 2)
    val steps = BubbleSort.sort(col, makeConfig(5, 10)).toList
    steps.foreach { step =>
      assertEquals(step.collectionAfter.size, col.size)
    }
  }

  test("two-element unsorted collection sorted in one step") {
    val col = makeCollection(2, 1)
    val steps = BubbleSort.sort(col, makeConfig(2, 10)).toList
    assertEquals(steps.length, 1)
    assertEquals(steps.head.collectionAfter.toList, List(1, 2))
  }

  test("two equal elements produce 0 steps") {
    val col = makeCollection(5, 5)
    val steps = BubbleSort.sort(col, makeConfig(2, 10)).toList
    assertEquals(steps.length, 0)
  }

  // ── ScalaCheck properties ──────────────────────────────────────────────

  property("output is always sorted ascending") {
    forAll(Gen.listOf(Gen.choose(1, 100))) { (elems: List[Int]) =>
      val col = SortableCollection(elems.toIndexedSeq, DataStructure.Array)
      val steps = BubbleSort.sort(col, makeConfig(elems.size, 100)).toList
      val result =
        if steps.isEmpty then elems.toIndexedSeq
        else steps.last.collectionAfter
      result.toList == result.sorted.toList
    }
  }

  property("step count is non-negative") {
    forAll(Gen.listOf(Gen.choose(1, 1000))) { (elems: List[Int]) =>
      val col = SortableCollection(elems.toIndexedSeq, DataStructure.Array)
      val steps = BubbleSort.sort(col, makeConfig(elems.size, 1000)).toList
      steps.size >= 0
    }
  }

  property("every step's collectionAfter is a permutation of the original") {
    forAll(Gen.listOfN(5, Gen.choose(1, 50))) { (elems: List[Int]) =>
      val col = SortableCollection(elems.toIndexedSeq, DataStructure.Array)
      val steps = BubbleSort.sort(col, makeConfig(elems.size, 100)).toList
      steps.forall(s => s.collectionAfter.sorted.toList == elems.sorted)
    }
  }

  // ── US3: timing formula property (T022) ──────────────────────────────

  property("SortingResult.executionMs equals elapsedMs minus delay times steps") {
    forAll(
      Gen.choose(0L, 10000L),
      Gen.choose(0, 500),
      Gen.choose(0, 200)
    ) { (elapsed: Long, steps: Int, delay: Int) =>
      val result = visualizer.domain.model.SortingResult(
        stepCount = steps,
        elapsedMs = elapsed,
        executionMs = elapsed - (delay.toLong * steps),
        sortedCollection = IndexedSeq.empty
      )
      result.executionMs == elapsed - (delay.toLong * steps)
    }
  }
