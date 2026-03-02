package visualizer.gui

import java.awt.{Color, Dimension, Graphics}
import javax.swing.JPanel
import visualizer.cli.CliConfig
import visualizer.domain.model.SortingStep

/** JPanel subclass that renders the collection as a vertical bar chart.
  *
  * @param config
  *   CLI config (provides maxElementSize for bar height scaling)
  * @param initialElements
  *   the unsorted collection so bars are visible before the first step
  */
class BarChartPanel(config: CliConfig, initialElements: IndexedSeq[Int]) extends JPanel:
  setPreferredSize(Dimension(800, 600))
  setBackground(Color.BLACK)

  // Written from the main thread, read from the EDT — must be volatile.
  @volatile private var elements: IndexedSeq[Int]       = initialElements
  @volatile private var highlightedPair: (Int, Int)     = (-1, -1)

  def updateStep(step: SortingStep): Unit =
    elements        = step.collectionAfter
    highlightedPair = step.movedPositions

  override def paintComponent(g: Graphics): Unit =
    super.paintComponent(g)
    val elems = elements
    val n     = elems.size
    if n == 0 then
      g.setColor(Color.GRAY)
      g.drawString("(empty collection)", 10, 30)
      return
    val w              = getWidth
    val h              = getHeight
    val barW           = math.max(1, w / n)
    val (hi, hj)       = highlightedPair
    elems.zipWithIndex.foreach { (v, i) =>
      val barH = ((v.toDouble / config.maxElementSize) * (h - 4)).toInt.max(1)
      val x    = i * barW
      val y    = h - barH
      g.setColor(if i == hi || i == hj then Color.RED else Color.CYAN)
      g.fillRect(x, y, (barW - 1).max(1), barH)
    }
