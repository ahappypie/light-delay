package io.github.ahappypie.LightDelay

import vsop87.functions._
import vsop87.{VSOPDataset, earth, mars}
import akka.actor.{Actor, Props}
import io.github.ahappypie.LightDelay.grpc.{SingleRequest, SingleResponse}

/**
  * Inspired by Emory Department of Physics
  * www.physics.emory.edu/astronomy/events/mars/calc.html
 */

object SingleDelayActor {
  def props: Props = Props(new SingleDelayActor)
}

class SingleDelayActor extends Actor {
  override def receive: Receive = {
    case req: SingleRequest =>
      sender ! SingleResponse(process(req))
  }

  def process(request: SingleRequest): Int = {
    val origin: Option[VSOPDataset] = registeredDatasets.get(request.origin.value)
    val destination: Option[VSOPDataset] = registeredDatasets.get(request.dest.value)

    delay(origin.getOrElse(earth), destination.getOrElse(mars), request.timestamp)
  }
}
