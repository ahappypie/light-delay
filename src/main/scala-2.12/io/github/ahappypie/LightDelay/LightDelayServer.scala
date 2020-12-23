package io.github.ahappypie.LightDelay

import java.util.logging.Logger
import akka.actor.{ActorRef, ActorSystem}
import akka.grpc.scaladsl.WebHandler
import akka.http.scaladsl.{Http, HttpConnectionContext}
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.pattern.ask
import akka.stream.{ActorMaterializer, Materializer}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import io.github.ahappypie.LightDelay.grpc.{AllRequest, AllResponse, LightDelay, LightDelayHandler, SingleRequest, SingleResponse}

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

object LightDelayServer {
  private val logger = Logger.getLogger(classOf[LightDelayServer].getName)

  def main(args: Array[String]): Unit = {
    val conf = ConfigFactory
      .parseString("akka.http.server.preview.enable-http2 = on")
      .withFallback(ConfigFactory.defaultApplication())
    val actorSystem = ActorSystem("light-delay-system", conf)
    val server = new LightDelayServer(actorSystem)
    server.run(sys.env.getOrElse("SERVE_WEB", "false").toBoolean)
  }

}

class LightDelayServer(actorSystem: ActorSystem) {
  implicit val system: ActorSystem = actorSystem
  implicit val mat: Materializer = ActorMaterializer()
  implicit val ec: ExecutionContext = system.dispatcher

  def run(web: Boolean = false): Future[Http.ServerBinding] = {
    // Create service handlers
    val service: PartialFunction[HttpRequest, Future[HttpResponse]] =
      LightDelayHandler.partial(new LightDelayImpl())
    // Bind service handler servers
    val binding = Http().bindAndHandleAsync(
      service,
      interface = "0.0.0.0",
      sys.env.getOrElse("GRPC_PORT", "50050").toInt,
      connectionContext = HttpConnectionContext())

    if(web) { runWeb(service) }

    // report successful binding
    binding.foreach { binding => println(s"gRPC server bound to: ${binding.localAddress}") }

    binding
  }

  private def runWeb(service: PartialFunction[HttpRequest, Future[HttpResponse]]): Future[Http.ServerBinding] = {
    val webHandler = WebHandler.grpcWebHandler(service)

    val binding = Http().bindAndHandleAsync(
      webHandler,
      interface = "0.0.0.0",
      sys.env.getOrElse("GRPC_WEB_PORT", "50051").toInt,
      connectionContext = HttpConnectionContext())

    // report successful binding
    binding.foreach { binding => println(s"gRPC Web server bound to: ${binding.localAddress}") }

    binding
  }

  private class LightDelayImpl extends LightDelay {
    implicit val timeout: Timeout = Timeout(5 seconds)
    val delaySupervisor: ActorRef = actorSystem.actorOf(DelaySupervisor.props, "delay-supervisor")

    override def getSingleDelay(req: SingleRequest): Future[SingleResponse] = {
      delaySupervisor.ask(req).mapTo[SingleResponse]
    }

    override def getAllDelay(req: AllRequest): Future[AllResponse] = {
      delaySupervisor.ask(req).mapTo[AllResponse]
    }
  }

}