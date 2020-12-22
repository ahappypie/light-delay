package io.github.ahappypie.LightDelay

import vsop87.functions._
import vsop87.{VSOPDataset, mercury, venus, earth, mars, jupiter, saturn, uranus, neptune}
import akka.actor.{Actor, Props}
import io.github.ahappypie.LightDelay.grpc.{LightDelayRequest, LightDelayResponse}

/**
  * Inspired by Emory Department of Physics
  * www.physics.emory.edu/astronomy/events/mars/calc.html
 */

object DelayActor {
  def props: Props = Props(new DelayActor)
}

class DelayActor extends Actor {
  override def receive: Receive = {
    case req: LightDelayRequest =>
      sender ! LightDelayResponse(process(req))
  }

  def process(request: LightDelayRequest): Int = {
    val origin: Option[VSOPDataset] = convertToDataset(request.origin)
    val destination: Option[VSOPDataset] = convertToDataset(request.dest)

    delay(origin.getOrElse(earth), destination.getOrElse(mars), request.timestamp)
  }

  private def convertToDataset(body: LightDelayRequest.Body): Option[VSOPDataset] = {
    if(body.isUnknownPlanet) {
      None
    } else if(body.isMercury) {
      Some(mercury)
    } else if(body.isVenus) {
      Some(venus)
    } else if(body.isEarth) {
      Some(earth)
    } else if(body.isMars) {
      Some(mars)
    } else if(body.isJupiter) {
      Some(jupiter)
    } else if(body.isSaturn) {
      Some(saturn)
    }else if(body.isUranus) {
      Some(uranus)
    } else if(body.isNeptune) {
      Some(neptune)
    } else {
      None
    }
  }
}
