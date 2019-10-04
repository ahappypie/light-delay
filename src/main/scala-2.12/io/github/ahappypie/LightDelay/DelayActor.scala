package io.github.ahappypie.LightDelay

import vsop87.functions._
import vsop87.{VSOPDataset, earth, jupiter, mars, saturn}
import akka.actor.{Actor, Props}
import io.github.ahappypie.LightDelay.grpc.delay.{LightDelayRequest, LightDelayResponse}

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
    val origin: Option[VSOPDataset] = getBody(request.origin.name)
    val destination: Option[VSOPDataset] = getBody(request.destination.name)

    delay(origin.getOrElse(earth), destination.getOrElse(mars), request.timestamp)
  }

  def getBody(b: String): Option[VSOPDataset] =  {
    b match {
      case "EARTH" => Some(earth)
      case "MARS" => Some(mars)
      case "JUPITER" => Some(jupiter)
      case "SATURN" => Some(saturn)
      case _ => None
    }
  }
}
