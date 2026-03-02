package visualizer.domain.model

/** In-memory collection type. CLI tokens match the `entryName` field. */
enum DataStructure(val entryName: String):
  case List extends DataStructure("list")
  case Array extends DataStructure("array")
  case Vector extends DataStructure("vector")
