package visualizer.gui

import java.awt.GraphicsEnvironment
import javax.swing.*
import visualizer.cli.CliConfig
import visualizer.domain.model.{SortableCollection, SortingStep}

/** Manages the Swing bar-chart window lifecycle.
  *
  * All Swing calls are dispatched to the EDT. The main thread only calls
  * `open()`, `update()`, and `awaitClose()`.
  */
class GuiRenderer(config: CliConfig, initialCollection: SortableCollection):

  private val panel = new BarChartPanel(config, initialCollection.elements)

  // Written by EDT, read by main thread — must be volatile.
  @volatile private var closed = false

  /** Create and show the JFrame on the EDT; blocks until the window is visible. */
  def open(): Unit =
    SwingUtilities.invokeAndWait { () =>
      val frame = new JFrame("Sorting Visualizer")
      frame.setUndecorated(true)  // window = bar chart, no title bar / OS border
      frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE)
      frame.add(panel)
      frame.pack()
      frame.setLocationRelativeTo(null)  // center on screen (called after pack)

      // ESC closes the window
      val escKey = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0)
      frame.getRootPane.registerKeyboardAction(
        _ => { frame.dispose(); closed = true },
        escKey,
        JComponent.WHEN_IN_FOCUSED_WINDOW
      )

      frame.addWindowListener(new java.awt.event.WindowAdapter:
        override def windowClosed(e: java.awt.event.WindowEvent): Unit = closed = true
      )

      frame.setVisible(true)
    }

  /** Push a new SortingStep to the bar chart on the EDT. */
  def update(step: SortingStep): Unit =
    panel.updateStep(step)
    SwingUtilities.invokeLater(() => panel.repaint())

  /** Block the main thread until the user closes the window (ESC or ✕). */
  def awaitClose(): Unit =
    while !closed do Thread.sleep(50)

object GuiRenderer:

  /** Call before any Swing/AWT object is constructed when --gui is active.
    * Exits with code 2 if no display is available.
    */
  def assertDisplayAvailable(): Unit =
    if GraphicsEnvironment.isHeadless then
      System.err.println(
        "Error: no display available — cannot open GUI window. " +
          "Run the tool without --gui on a headless system."
      )
      sys.exit(2)
