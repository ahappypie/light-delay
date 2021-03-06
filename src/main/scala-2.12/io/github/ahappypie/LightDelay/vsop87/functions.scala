package io.github.ahappypie.LightDelay.vsop87

import java.time.{Instant, LocalDateTime, ZoneId}

object functions {
  def julianDate(timestamp: Long): Double = {
    val date = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.of("Z"))
    var year = date.getYear
    var month = date.getMonthValue
    if (month < 3) {
      year = year - 1
      month = month + 12
    }
    val day = date.getDayOfMonth
    val hour = date.getHour
    val min = date.getMinute
    val sec = date.getSecond

    val A = year / 100
    val B = 2 - A + (A / 4)
    val E = (hour / 24) + (min / 1440) + (sec / (8.64 * Math.pow(10, 4)))

    (365.25 * (year + 4716)) + (30.6001 * (month + 1)) + day + B - 1524.5 + E
  }

  def calc_series(array: Array[(Double, Double, Double)], t: Double): Double = {
    var value = 0.0
    for (tuple <- array) {
      value += tuple._1 * Math.cos(tuple._2 + (tuple._3 * t))
    }
    value
  }

  def helio(planet: VSOPDataset, jd: Double): (Double, Double, Double) = {
    val t = (jd - 2451545.0) / 365250.0
    val t2 = t * t
    val t3 = t2 * t
    val t4 = t3 * t
    val t5 = t4 * t

    val L0 = calc_series(planet.long.l0, t)
    val L1 = calc_series(planet.long.l1, t)
    val L2 = calc_series(planet.long.l2, t)
    val L3 = calc_series(planet.long.l3, t)
    val L4 = calc_series(planet.long.l4, t)
    val L5 = calc_series(planet.long.l5, t)
    val L = (L0 + L1 * t + L2 * t2 + L3 * t3 + L4 * t4 + L5 * t5)

    val B0 = calc_series(planet.lat.b0, t)
    val B1 = calc_series(planet.lat.b1, t)
    val B2 = calc_series(planet.lat.b2, t)
    val B3 = calc_series(planet.lat.b3, t)
    val B4 = calc_series(planet.lat.b4, t)
    val B5 = calc_series(planet.lat.b5, t)
    val B = (B0 + B1 * t + B2 * t2 + B3 * t3 + B4 * t4 + B5 * t5)

    val R0 = calc_series(planet.rad.r0, t)
    val R1 = calc_series(planet.rad.r1, t)
    val R2 = calc_series(planet.rad.r2, t)
    val R3 = calc_series(planet.rad.r3, t)
    val R4 = calc_series(planet.rad.r4, t)
    val R5 = calc_series(planet.rad.r5, t)
    val R = (R0 + R1 * t + R2 * t2 + R3 * t3 + R4 * t4 + R5 * t5)

    (L, B, R)
  }

  def delay(origin: VSOPDataset, destination: VSOPDataset, timestamp: Long): Int = {
    val jd = julianDate(timestamp)
    val ori = origin.helio(origin, jd)
    val dest = destination.helio(destination, jd)
    val d = distance(ori, dest, jd)
    val lt = lightTime(d)

    val jd2 = jd - lt
    val ori2 = origin.helio(origin, jd2)
    val dest2 = destination.helio(destination, jd2)
    val d2 = distance(ori2, dest2, jd2)
    val lt2 = lightTime(d2)

    //delay in ms
    Math.round(lt2 * 1440 * 60 * 1000).toInt
  }

  //In AU
  def distance(earth: (Double, Double, Double), mars: (Double, Double, Double), jd: Double): Double = {
    val x = (mars._3 * Math.cos(mars._2) * Math.cos(mars._1)) - (earth._3 * Math.cos(earth._1) * Math.cos(earth._2))
    val y = (mars._3 * Math.cos(mars._2) * Math.sin(mars._1)) - (earth._3 * Math.sin(earth._1) * Math.cos(earth._2))
    val z = (mars._3 * Math.sin(mars._2)) - (earth._3 * Math.sin(earth._2))

    val t = (jd - 2451545.0) / 365250.0
    val Q = 3.8048177 + (8399.711184 * t)
    val u = x - (Math.cos(Q) * 0.0000312)
    val v = y - (Math.sin(Q) * 0.0000286)
    val w = z - (Math.sin(Q) * 0.0000124)

    Math.sqrt(Math.pow(u, 2) + Math.pow(v, 2) + Math.pow(w, 2))
  }

  //In days
  def lightTime(distance: Double): Double = {
    distance * .0057755183
  }

  val registeredDatasets: Map[Int, VSOPDataset] = Map(
    1 -> mercury,
    2 -> venus,
    3 -> earth,
    4 -> mars,
    5 -> jupiter,
    6 -> saturn,
    7 -> uranus,
    8 -> neptune
  )
}