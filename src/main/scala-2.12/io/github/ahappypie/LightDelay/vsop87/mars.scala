package io.github.ahappypie.LightDelay.vsop87

object mars extends VSOPDataset {
  override val lat: Latitude = data.mars.lat
  override val long: Longitude = data.mars.long
  override val rad: Radius = data.mars.rad
}