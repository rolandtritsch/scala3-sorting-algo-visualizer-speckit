package visualizer.domain.model

/** An in-memory collection of integers being sorted.
  *
  * Every mutation returns a new instance; no in-place state is mutated.
  */
final case class SortableCollection(
    elements: IndexedSeq[Int],
    dataStructure: DataStructure
):
  def size: Int = elements.size
  def isEmpty: Boolean = elements.isEmpty
