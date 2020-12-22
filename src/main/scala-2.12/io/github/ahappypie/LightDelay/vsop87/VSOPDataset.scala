package io.github.ahappypie.LightDelay.vsop87

trait VSOPDataset {
  val lat: Latitude
  val long: Longitude
  val rad: Radius

  def helio(planet: VSOPDataset, jd: Double) = functions.helio(planet, jd)
}
