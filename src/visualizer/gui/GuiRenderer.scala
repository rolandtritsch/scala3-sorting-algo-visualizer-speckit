package visualizer.gui

import java.awt.GraphicsEnvironment
import visualizer.cli.CliConfig
import visualizer.domain.model.SortingStep

/** Manages the Swing bar-chart window lifecycle. */
class GuiRenderer(config: CliConfig):
  private val panel = new BarChartPanel(config)
  private var closed = false

  /** Open the centered JFrame. Must be called on the main thread. */
  def open(): Unit =
    import javax.swing.*
    val frame = new JFrame("Sorting Visualizer")
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE)
    frame.add(panel)
    frame.setSize(800, 600)
    frame.setLocationRelativeTo(null)

    // ESC closes the window
    val escKey = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0)
    frame.getRootPane.registerKeyboardAction(
      _ => { frame.dispose(); closed = true },
      escKey,
      javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW
    )

    frame.addWindowListener(new java.awt.event.WindowAdapter:
      override def windowClosed(e: java.awt.event.WindowEvent): Unit = closed = true
    )

    frame.setVisible(true)

  /** Push a new SortingStep to the bar chart on the EDT. */
  def update(step: SortingStep, cfg: CliConfig): Unit =
    panel.updateStep(step, cfg)
    javax.swing.SwingUtilities.invokeLater(() => panel.repaint())

  /** Block until the window is closed by the user (ESC or close button). */
  def awaitClose(): Unit =
    while !closed do Thread.sleep(50)

object GuiRenderer:

  /** Call before any Swing/AWT object is constructed when --gui is active. Exits with code 2 if no
    * display is available.
    */
  def assertDisplayAvailable(): Unit =
    if GraphicsEnvironment.isHeadless then
      System.err.println(
        "Error: no display available — cannot open GUI window. " +
          "Run the tool without --gui on a headless system."
      )
      sys.exit(2)
