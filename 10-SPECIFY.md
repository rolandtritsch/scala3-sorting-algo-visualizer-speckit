# scala3-sorting-algo-visualizer-speckit - Specify - What to build

Build a CLI tool called Visualizer. 

The main takes the following command-line parameters: 

* --number-of-elements
* --max-element-size,
* --delay-between-steps
* --algorithm <quick-sort|bubble-sort>
* --data-structure <list|array|vector>
* --gui (optional)

The tool will generate a data-structure with number-of-elements in it. The elements will be in the range of 1 to max-element-size.

It will then sort the data-structure with the given algorithm.

With the gui flag it will display a window in the middle of the acreen, that will show the sorting of the array every step of the way as a bar-chart. When the sorting is done the window can be closed with the ESC key.
