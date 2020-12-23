package io.github.ahappypie.LightDelay

import akka.actor.{Actor, Props}
import io.github.ahappypie.LightDelay.grpc.{AllRequest, AllResponse, Body, DelayEntry}
import io.github.ahappypie.LightDelay.vsop87.functions.{delay, registeredDatasets}
import io.github.ahappypie.LightDelay.vsop87.VSOPDataset

import scala.collection.mutable.ListBuffer

object AllDelayActor {
  def props: Props = Props(new AllDelayActor)
}

class AllDelayActor extends Actor {
  override def receive: Receive = {
    case req: AllRequest =>
      sender ! AllResponse(process(req))
  }

  def process(request: AllRequest): Seq[DelayEntry] = {
    val entries = new ListBuffer[DelayEntry]
    val origin: VSOPDataset = registeredDatasets(request.origin.value)
    val destinations: Map[Int, VSOPDataset] = registeredDatasets.filter(_._1 != request.origin.value)

    for(dest <- destinations) {
      entries += DelayEntry(Body.fromValue(dest._1), delay(origin, dest._2, request.timestamp))
    }
    entries.sortBy(_.body.value)
  }
}
