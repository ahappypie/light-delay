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
    var origin: Option[VSOPDataset] = None
    var destination: Option[VSOPDataset] = None

    request.origin.name match {
      case "EARTH" => origin = Some(earth)
      case "MARS" => origin = Some(mars)
      case "JUPITER" => origin = Some(jupiter)
      case "SATURN" => origin = Some(saturn)
      case _ => None
    }

    request.dest.name match {
      case "EARTH" => destination = Some(earth)
      case "MARS" => destination = Some(mars)
      case "JUPITER" => destination = Some(jupiter)
      case "SATURN" => destination = Some(saturn)
      case _ => None
    }

    delay(origin.getOrElse(earth), destination.getOrElse(mars), request.timestamp)
  }
}
