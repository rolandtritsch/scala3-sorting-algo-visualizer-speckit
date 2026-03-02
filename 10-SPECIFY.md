# scala3-sorting-algo-visualizer-speckit - Specify

Build a CLI tool called Visualizer. 

The main takes the following command-line parameters: 

* --number-of-elements
* --max-element-size,
* --delay-between-steps-ms
* --algorithm <quick-sort|bubble-sort>
* --data-structure <list|array|vector>
* --gui (optional)

The tool will generate a data-structure with number-of-elements in it. The
elements will be in the range of 1 to max-element-size. The elements do not need
to be unique.

It will then sort the data-structure with the given algorithm.

While sorting one high-level info log-messages should show the progress with
every step/iteration of the sorting. All the other (more) low-level log-messages
should be debug messages.

To make it easier to observer and understand the sorting we are going to sleep
for delay-between-steps-ms milliseconds between steps.

At the end we want to display an info log-message with the elapse time (in
milliseconds) it took to sort the data-structure. We also want to show the
execution time (elapse-time - (delay-between-steps-ms * number-of-steps)).

With the gui flag it will display a window in the middle of the acreen, that
will show the sorting of the array every step of the way as a bar-chart. When
the sorting is done the window can be closed with the ESC key.

At the end ...

- README.md - should documented what this repo is doing/implementing and how to
  use it
- CLAUDE.md - should documented how the visualizer is architected and designed
  and implemented
- CONTRUBUTING.md - should document how to make changes and how to
  build/test/run the changes
