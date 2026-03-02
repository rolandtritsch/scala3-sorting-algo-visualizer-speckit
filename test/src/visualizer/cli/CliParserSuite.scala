package visualizer.cli

import munit.FunSuite
import visualizer.domain.model.{Algorithm, DataStructure}

class CliParserSuite extends FunSuite:

  // ── happy path ──────────────────────────────────────────────────────────

  test("parses all required flags successfully") {
    val args = Seq(
      "--number-of-elements",
      "10",
      "--max-element-size",
      "100",
      "--algorithm",
      "bubble-sort",
      "--data-structure",
      "array"
    )
    CliParser.parse(args) match
      case Right(cfg) =>
        assertEquals(cfg.numberOfElements, 10)
        assertEquals(cfg.maxElementSize, 100)
        assertEquals(cfg.delayBetweenStepsMs, 0)
        assertEquals(cfg.algorithm, Algorithm.BubbleSort)
        assertEquals(cfg.dataStructure, DataStructure.Array)
        assertEquals(cfg.gui, false)
      case Left(err) => fail(s"Expected Right, got Left: $err")
  }

  test("parses quick-sort and vector data structure") {
    val args = Seq(
      "--number-of-elements",
      "5",
      "--max-element-size",
      "50",
      "--algorithm",
      "quick-sort",
      "--data-structure",
      "vector"
    )
    CliParser.parse(args) match
      case Right(cfg) =>
        assertEquals(cfg.algorithm, Algorithm.QuickSort)
        assertEquals(cfg.dataStructure, DataStructure.Vector)
      case Left(err) => fail(s"Unexpected error: $err")
  }

  test("parses list data structure") {
    val args = Seq(
      "--number-of-elements",
      "3",
      "--max-element-size",
      "10",
      "--algorithm",
      "bubble-sort",
      "--data-structure",
      "list"
    )
    CliParser.parse(args) match
      case Right(cfg) => assertEquals(cfg.dataStructure, DataStructure.List)
      case Left(err) => fail(s"Unexpected error: $err")
  }

  test("delay defaults to 0 when not supplied") {
    val args = Seq(
      "--number-of-elements",
      "5",
      "--max-element-size",
      "10",
      "--algorithm",
      "bubble-sort",
      "--data-structure",
      "array"
    )
    CliParser.parse(args) match
      case Right(cfg) => assertEquals(cfg.delayBetweenStepsMs, 0)
      case Left(err) => fail(s"Unexpected error: $err")
  }

  test("parses explicit delay-between-steps-ms") {
    val args = Seq(
      "--number-of-elements",
      "5",
      "--max-element-size",
      "10",
      "--delay-between-steps-ms",
      "200",
      "--algorithm",
      "bubble-sort",
      "--data-structure",
      "array"
    )
    CliParser.parse(args) match
      case Right(cfg) => assertEquals(cfg.delayBetweenStepsMs, 200)
      case Left(err) => fail(s"Unexpected error: $err")
  }

  test("--gui flag is false when absent") {
    val args = Seq(
      "--number-of-elements",
      "5",
      "--max-element-size",
      "10",
      "--algorithm",
      "bubble-sort",
      "--data-structure",
      "array"
    )
    CliParser.parse(args) match
      case Right(cfg) => assertEquals(cfg.gui, false)
      case Left(err) => fail(s"Unexpected error: $err")
  }

  test("--gui flag is true when present") {
    val args = Seq(
      "--number-of-elements",
      "5",
      "--max-element-size",
      "10",
      "--algorithm",
      "bubble-sort",
      "--data-structure",
      "array",
      "--gui"
    )
    CliParser.parse(args) match
      case Right(cfg) => assertEquals(cfg.gui, true)
      case Left(err) => fail(s"Unexpected error: $err")
  }

  test("accepts zero number-of-elements (empty collection)") {
    val args = Seq(
      "--number-of-elements",
      "0",
      "--max-element-size",
      "10",
      "--algorithm",
      "bubble-sort",
      "--data-structure",
      "array"
    )
    CliParser.parse(args) match
      case Right(cfg) => assertEquals(cfg.numberOfElements, 0)
      case Left(err) => fail(s"Unexpected error: $err")
  }

  // ── error cases ─────────────────────────────────────────────────────────

  test("returns Left when --number-of-elements is missing") {
    val args = Seq(
      "--max-element-size",
      "10",
      "--algorithm",
      "bubble-sort",
      "--data-structure",
      "array"
    )
    assert(CliParser.parse(args).isLeft)
  }

  test("returns Left when --max-element-size is missing") {
    val args = Seq(
      "--number-of-elements",
      "5",
      "--algorithm",
      "bubble-sort",
      "--data-structure",
      "array"
    )
    assert(CliParser.parse(args).isLeft)
  }

  test("returns Left when --algorithm is missing") {
    val args = Seq(
      "--number-of-elements",
      "5",
      "--max-element-size",
      "10",
      "--data-structure",
      "array"
    )
    assert(CliParser.parse(args).isLeft)
  }

  test("returns Left when --data-structure is missing") {
    val args = Seq(
      "--number-of-elements",
      "5",
      "--max-element-size",
      "10",
      "--algorithm",
      "bubble-sort"
    )
    assert(CliParser.parse(args).isLeft)
  }

  test("returns Left for invalid algorithm token") {
    val args = Seq(
      "--number-of-elements",
      "5",
      "--max-element-size",
      "10",
      "--algorithm",
      "merge-sort",
      "--data-structure",
      "array"
    )
    assert(CliParser.parse(args).isLeft)
  }

  test("returns Left for invalid data-structure token") {
    val args = Seq(
      "--number-of-elements",
      "5",
      "--max-element-size",
      "10",
      "--algorithm",
      "bubble-sort",
      "--data-structure",
      "linked-list"
    )
    assert(CliParser.parse(args).isLeft)
  }

  test("returns Left for negative number-of-elements") {
    val args = Seq(
      "--number-of-elements",
      "-1",
      "--max-element-size",
      "10",
      "--algorithm",
      "bubble-sort",
      "--data-structure",
      "array"
    )
    assert(CliParser.parse(args).isLeft)
  }

  test("returns Left for zero max-element-size") {
    val args = Seq(
      "--number-of-elements",
      "5",
      "--max-element-size",
      "0",
      "--algorithm",
      "bubble-sort",
      "--data-structure",
      "array"
    )
    assert(CliParser.parse(args).isLeft)
  }
