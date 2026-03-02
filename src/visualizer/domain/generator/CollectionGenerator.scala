package visualizer.domain.generator

import scala.util.Random
import visualizer.cli.CliConfig
import visualizer.domain.model.SortableCollection

object CollectionGenerator:

  /** Generate a SortableCollection from the given CLI config.
    *
    * Elements are random integers in [1, maxElementSize]. Values are not required to be unique. An
    * empty collection is returned when numberOfElements == 0.
    */
  def generate(config: CliConfig): SortableCollection =
    val elements: IndexedSeq[Int] =
      if config.numberOfElements == 0 then IndexedSeq.empty
      else
        IndexedSeq.fill(config.numberOfElements)(
          Random.between(1, config.maxElementSize + 1)
        )
    SortableCollection(elements, config.dataStructure)
