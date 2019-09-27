package io.github.ahappypie.LightDelay

import akka.actor.{Actor, Props}

import io.github.ahappypie.LightDelay.grpc.delay.LightDelayRequest

object DelaySupervisor {
  def props: Props = Props(new DelaySupervisor)
}

class DelaySupervisor extends Actor {
  override def receive: Receive = {
    case req: LightDelayRequest => context.actorOf(DelayActor.props).forward(req)
  }
}
