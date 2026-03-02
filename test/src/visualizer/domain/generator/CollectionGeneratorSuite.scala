package visualizer.domain.generator

import munit.ScalaCheckSuite
import org.scalacheck.Prop.*
import org.scalacheck.Gen
import visualizer.cli.CliConfig
import visualizer.domain.model.{Algorithm, DataStructure}

class CollectionGeneratorSuite extends ScalaCheckSuite:

  private def makeConfig(n: Int, maxSize: Int): CliConfig =
    CliConfig(
      numberOfElements = n,
      maxElementSize = maxSize,
      delayBetweenStepsMs = 0,
      algorithm = Algorithm.BubbleSort,
      dataStructure = DataStructure.Array,
      gui = false
    )

  // ── unit tests ──────────────────────────────────────────────────────────

  test("generates collection with the requested number of elements") {
    val col = CollectionGenerator.generate(makeConfig(10, 100))
    assertEquals(col.size, 10)
  }

  test("all elements are within [1, maxElementSize]") {
    val col = CollectionGenerator.generate(makeConfig(50, 20))
    col.elements.foreach { e =>
      assert(e >= 1 && e <= 20, s"Element $e outside [1, 20]")
    }
  }

  test("generates empty collection when numberOfElements is 0") {
    val col = CollectionGenerator.generate(makeConfig(0, 100))
    assert(col.isEmpty)
    assertEquals(col.size, 0)
  }

  test("generates single-element collection") {
    val col = CollectionGenerator.generate(makeConfig(1, 100))
    assertEquals(col.size, 1)
    assert(col.elements.head >= 1 && col.elements.head <= 100)
  }

  test("when maxElementSize is 1, all elements are 1") {
    val col = CollectionGenerator.generate(makeConfig(20, 1))
    col.elements.foreach(e => assertEquals(e, 1))
  }

  test("preserves dataStructure in the result") {
    val config =
      makeConfig(5, 10).copy(dataStructure = DataStructure.Vector)
    val col = CollectionGenerator.generate(config)
    assertEquals(col.dataStructure, DataStructure.Vector)
  }

  // ── ScalaCheck properties ──────────────────────────────────────────────

  property("generated length always equals numberOfElements") {
    forAll(Gen.choose(0, 200), Gen.choose(1, 1000)) { (n: Int, maxSize: Int) =>
      val col = CollectionGenerator.generate(makeConfig(n, maxSize))
      col.size == n
    }
  }

  property("all elements satisfy 1 <= e <= maxElementSize") {
    forAll(Gen.choose(1, 100), Gen.choose(1, 500)) { (n: Int, maxSize: Int) =>
      val col = CollectionGenerator.generate(makeConfig(n, maxSize))
      col.elements.forall(e => e >= 1 && e <= maxSize)
    }
  }

  property("empty collection when numberOfElements is 0") {
    forAll(Gen.choose(1, 1000)) { (maxSize: Int) =>
      val col = CollectionGenerator.generate(makeConfig(0, maxSize))
      col.isEmpty
    }
  }
