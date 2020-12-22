package io.github.ahappypie.LightDelay

import java.util.logging.Logger
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.{Http, HttpConnectionContext}
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.pattern.ask
import akka.stream.{ActorMaterializer, Materializer}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import io.github.ahappypie.LightDelay.grpc.{LightDelay, LightDelayHandler, LightDelayRequest, LightDelayResponse}

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

object LightDelayServer {
  private val logger = Logger.getLogger(classOf[LightDelayServer].getName)

  def main(args: Array[String]): Unit = {
    val conf = ConfigFactory
      .parseString("akka.http.server.preview.enable-http2 = on")
      .withFallback(ConfigFactory.defaultApplication())
    val actorSystem = ActorSystem("light-delay-system", conf)
    new LightDelayServer(actorSystem).run()
  }

}

class LightDelayServer(actorSystem: ActorSystem) {

  def run(): Future[Http.ServerBinding] = {
    implicit val sys: ActorSystem = actorSystem
    implicit val mat: Materializer = ActorMaterializer()
    implicit val ec: ExecutionContext = sys.dispatcher

    // Create service handlers
    val service: HttpRequest => Future[HttpResponse] =
      LightDelayHandler(new LightDelayImpl())

    // Bind service handler servers to localhost:8080/8081
    val binding = Http().bindAndHandleAsync(
      service,
      interface = "127.0.0.1",
      port = 50000,
      connectionContext = HttpConnectionContext())

    // report successful binding
    binding.foreach { binding => println(s"gRPC server bound to: ${binding.localAddress}") }

    binding
  }

  private class LightDelayImpl extends LightDelay {
    implicit val timeout: Timeout = Timeout(5 seconds)
    val delaySupervisor: ActorRef = actorSystem.actorOf(DelaySupervisor.props, "delay-supervisor")

    override def getLightDelay(req: LightDelayRequest): Future[LightDelayResponse] = {
      delaySupervisor.ask(req).mapTo[LightDelayResponse]
    }
  }

}