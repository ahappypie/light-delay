package io.github.ahappypie.LightDelay.vsop87

object venus extends VSOPDataset {
  override val lat: Latitude = data.venus.lat
  override val long: Longitude = data.venus.long
  override val rad: Radius = data.venus.rad
}
