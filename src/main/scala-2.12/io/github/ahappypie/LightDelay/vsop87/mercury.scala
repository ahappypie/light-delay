package io.github.ahappypie.LightDelay.vsop87

object mercury extends VSOPDataset {
  override val lat: Latitude = data.mercury.lat
  override val long: Longitude = data.mercury.long
  override val rad: Radius = data.mercury.rad
}
