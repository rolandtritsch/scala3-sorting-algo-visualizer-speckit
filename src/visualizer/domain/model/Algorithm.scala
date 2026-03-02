package visualizer.domain.model

/** Sorting algorithm to apply. CLI tokens match the `entryName` field. */
enum Algorithm(val entryName: String):
  case BubbleSort extends Algorithm("bubble-sort")
  case QuickSort extends Algorithm("quick-sort")
