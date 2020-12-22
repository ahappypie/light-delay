package io.github.ahappypie.LightDelay.vsop87

import io.github.ahappypie.LightDelay.vsop87.functions.calc_series

object neptune extends VSOPDataset {
  override val lat: Latitude = data.neptune.lat
  override val long: Longitude = data.neptune.long
  override val rad: Radius = data.neptune.rad

  override def helio(planet: VSOPDataset, jd: Double): (Double, Double, Double) = {
    val t = (jd - 2451545.0)/365250.0
    val t2 = t*t
    val t3 = t2*t
    val t4 = t3*t

    val L0 = calc_series(planet.long.l0, t)
    val L1 = calc_series(planet.long.l1, t)
    val L2 = calc_series(planet.long.l2, t)
    val L3 = calc_series(planet.long.l3, t)
    val L = (L0 + L1 * t + L2 * t2 + L3 * t3)

    val B0 = calc_series(planet.lat.b0, t)
    val B1 = calc_series(planet.lat.b1, t)
    val B2 = calc_series(planet.lat.b2, t)
    val B3 = calc_series(planet.lat.b3, t)
    val B = (B0 + B1 * t + B2 * t2 + B3 * t3)

    val R0 = calc_series(planet.rad.r0, t)
    val R1 = calc_series(planet.rad.r1, t)
    val R2 = calc_series(planet.rad.r2, t)
    val R3 = calc_series(planet.rad.r3, t)
    val R4 = calc_series(planet.rad.r4, t)
    val R = (R0 + R1 * t + R2 * t2 + R3 * t3 + R4 * t4)

    (L,B,R)
  }
}
