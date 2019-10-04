package io.github.ahappypie.LightDelay

import java.util.logging.Logger

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import io.grpc.{Server, ServerBuilder}
import io.github.ahappypie.LightDelay.grpc.delay.{LightDelayGrpc, LightDelayRequest, LightDelayResponse}
import spray.json.DefaultJsonProtocol._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

object LightDelayServer {
  private val logger = Logger.getLogger(classOf[LightDelayServer].getName)

  def main(args: Array[String]): Unit = {
    implicit val actorSystem = ActorSystem("light-delay-system")
    val delaySupervisor = actorSystem.actorOf(DelaySupervisor.props, "delay-supervisor")
    val server = new LightDelayServer(ExecutionContext.global, delaySupervisor)

    implicit val materializer = ActorMaterializer()
    case class JSONRequest(timestamp: Long, origin: Option[String], destination: Option[String])
    implicit val jsonRequestFormat = jsonFormat3(JSONRequest)

    val route = pathSingleSlash {
      post {
        entity(as[JSONRequest]) { req =>
          val t = LightDelayRequest(timestamp = req.timestamp, LightDelayRequest.Body.fromName(req.origin.getOrElse("EARTH")).get, LightDelayRequest.Body.fromName(req.destination.getOrElse("MARS")).get)

          val res: Future[LightDelayResponse] = delaySupervisor.ask(t)(Timeout(5 seconds)).mapTo[LightDelayResponse]

          onComplete(res) { i => complete(i.get.delay.toString) }
        }
      }
    }

    val bindingFuture = Http().bindAndHandle(route, "0.0.0.0", sys.env.getOrElse("API_PORT", "8080").toInt)
    println(s"Accepting JSON at http://0.0.0.0:${sys.env.getOrElse("API_PORT", "8080")}/")

    server.start()
    server.blockUntilShutdown()
  }

  private val port = sys.env.getOrElse("GRPC_PORT", "50051").toInt
}

class LightDelayServer(ec: ExecutionContext, ds: ActorRef) { self =>
  private[this] var server: Server = null

  private def start(): Unit = {
    val sb = ServerBuilder.forPort(LightDelayServer.port)
    sb.addService(LightDelayGrpc.bindService(new LightDelayImpl, ec))
    server = sb.build.start
    LightDelayServer.logger.info("gRPC server started, listening on " + LightDelayServer.port)
    sys.addShutdownHook {
      System.err.println("*** shutting down gRPC server since JVM is shutting down")
      self.stop()
      System.err.println("*** server shut down")
    }
  }

  private def stop(): Unit = {
    if (server != null) {
      server.shutdown()
    }
  }

  private def blockUntilShutdown(): Unit = {
    if (server != null) {
      server.awaitTermination()
    }
  }

  private class LightDelayImpl extends LightDelayGrpc.LightDelay {
    override def getLightDelay(req: LightDelayRequest) = {
      ds.ask(req)(Timeout(5 seconds)).mapTo[LightDelayResponse]
    }
  }

}