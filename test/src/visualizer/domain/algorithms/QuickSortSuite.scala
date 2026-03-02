package visualizer.domain.algorithms

import munit.ScalaCheckSuite
import org.scalacheck.Prop.*
import org.scalacheck.Gen
import visualizer.cli.CliConfig
import visualizer.domain.model.{Algorithm, DataStructure, SortableCollection}

class QuickSortSuite extends ScalaCheckSuite:

  private def makeConfig(n: Int, maxSize: Int): CliConfig =
    CliConfig(
      numberOfElements = n,
      maxElementSize = maxSize,
      delayBetweenStepsMs = 0,
      algorithm = Algorithm.QuickSort,
      dataStructure = DataStructure.Array,
      gui = false
    )

  private def makeCollection(elems: Int*): SortableCollection =
    SortableCollection(elems.toIndexedSeq, DataStructure.Array)

  // ── unit tests ──────────────────────────────────────────────────────────

  test("sorts a small unsorted collection") {
    val col = makeCollection(3, 1, 2)
    val steps = QuickSort.sort(col, makeConfig(3, 10)).toList
    val finalState =
      if steps.isEmpty then col.elements
      else steps.last.collectionAfter
    assertEquals(finalState.sorted.toList, finalState.toList)
  }

  test("empty collection produces 0 steps") {
    val col = makeCollection()
    val steps = QuickSort.sort(col, makeConfig(0, 10)).toList
    assertEquals(steps.length, 0)
  }

  test("single-element collection produces 0 steps") {
    val col = makeCollection(42)
    val steps = QuickSort.sort(col, makeConfig(1, 100)).toList
    assertEquals(steps.length, 0)
  }

  test("already-sorted collection produces 0 or more steps with sorted output") {
    val col = makeCollection(1, 2, 3, 4, 5)
    val steps = QuickSort.sort(col, makeConfig(5, 10)).toList
    val result =
      if steps.isEmpty then col.elements
      else steps.last.collectionAfter
    assertEquals(result.toList, List(1, 2, 3, 4, 5))
  }

  test("stepIndex is 1-based and monotonically increasing") {
    val col = makeCollection(5, 3, 1, 4, 2)
    val steps = QuickSort.sort(col, makeConfig(5, 10)).toList
    steps.zipWithIndex.foreach { (step, idx) =>
      assertEquals(step.stepIndex, idx + 1)
    }
  }

  test("movedPositions indices are valid") {
    val col = makeCollection(5, 3, 4, 1, 2)
    val steps = QuickSort.sort(col, makeConfig(5, 10)).toList
    steps.foreach { step =>
      val (i, j) = step.movedPositions
      assert(i >= 0 && i < col.size, s"index $i out of range")
      assert(j >= 0 && j < col.size, s"index $j out of range")
    }
  }

  test("collectionAfter length equals original size throughout") {
    val col = makeCollection(5, 3, 4, 1, 2)
    val steps = QuickSort.sort(col, makeConfig(5, 10)).toList
    steps.foreach { step =>
      assertEquals(step.collectionAfter.size, col.size)
    }
  }

  // ── ScalaCheck properties ──────────────────────────────────────────────

  property("output is always sorted ascending") {
    forAll(Gen.listOf(Gen.choose(1, 100))) { (elems: List[Int]) =>
      val col = SortableCollection(elems.toIndexedSeq, DataStructure.Array)
      val steps = QuickSort.sort(col, makeConfig(elems.size, 100)).toList
      val result =
        if steps.isEmpty then elems.toIndexedSeq
        else steps.last.collectionAfter
      result.toList == elems.sorted
    }
  }

  property("step count is non-negative") {
    forAll(Gen.listOf(Gen.choose(1, 1000))) { (elems: List[Int]) =>
      val col = SortableCollection(elems.toIndexedSeq, DataStructure.Array)
      val steps = QuickSort.sort(col, makeConfig(elems.size, 1000)).toList
      steps.size >= 0
    }
  }

  property("every step's collectionAfter is a permutation of the original") {
    forAll(Gen.listOfN(6, Gen.choose(1, 50))) { (elems: List[Int]) =>
      val col = SortableCollection(elems.toIndexedSeq, DataStructure.Array)
      val steps = QuickSort.sort(col, makeConfig(elems.size, 100)).toList
      steps.forall(s => s.collectionAfter.sorted.toList == elems.sorted)
    }
  }
