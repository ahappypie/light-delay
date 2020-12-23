package io.github.ahappypie.LightDelay

import akka.actor.{Actor, Props}
import io.github.ahappypie.LightDelay.grpc.{AllRequest, SingleRequest}

object DelaySupervisor {
  def props: Props = Props(new DelaySupervisor)
}

class DelaySupervisor extends Actor {
  override def receive: Receive = {
    case req: SingleRequest => context.actorOf(SingleDelayActor.props).forward(req)
    case req: AllRequest => context.actorOf(AllDelayActor.props).forward(req)
  }
}
