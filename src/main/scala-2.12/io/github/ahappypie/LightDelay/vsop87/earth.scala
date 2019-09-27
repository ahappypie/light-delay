package io.github.ahappypie.LightDelay.vsop87

object earth extends VSOPDataset {
  override val lat: Latitude = data.earth.lat
  override val long: Longitude = data.earth.long
  override val rad: Radius = data.earth.rad
}
