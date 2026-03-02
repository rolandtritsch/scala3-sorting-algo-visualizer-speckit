package visualizer.gui

import java.awt.{Color, Dimension, Graphics}
import javax.swing.JPanel
import visualizer.cli.CliConfig
import visualizer.domain.model.SortingStep

/** JPanel subclass that renders the collection as a vertical bar chart. */
class BarChartPanel(config: CliConfig) extends JPanel:
  setPreferredSize(Dimension(800, 600))
  setBackground(Color.WHITE)

  @volatile private var currentStep: Option[SortingStep] = None
  @volatile private var currentConfig: CliConfig = config

  def updateStep(step: SortingStep, cfg: CliConfig): Unit =
    currentStep = Some(step)
    currentConfig = cfg

  override def paintComponent(g: Graphics): Unit =
    super.paintComponent(g)
    currentStep match
      case None => drawInitial(g)
      case Some(step) =>
        val cfg = currentConfig
        val elems = step.collectionAfter
        val w = getWidth
        val h = getHeight
        val n = elems.size
        if n == 0 then return
        val barW = math.max(1, w / n)
        val (hi, hj) = step.movedPositions
        elems.zipWithIndex.foreach { (v, i) =>
          val barH = (v.toDouble / cfg.maxElementSize * (h - 4)).toInt
          val x = i * barW
          val y = h - barH
          g.setColor(if i == hi || i == hj then Color.RED else Color.DARK_GRAY)
          g.fillRect(x, y, barW - 1, barH)
        }

  private def drawInitial(g: Graphics): Unit =
    val elems = config match
      case c if c.numberOfElements == 0 => IndexedSeq.empty[Int]
      case _ => IndexedSeq.empty[Int] // pre-sort state not stored here
    g.setColor(Color.GRAY)
    g.drawString("Waiting for sort to start...", 10, 30)
