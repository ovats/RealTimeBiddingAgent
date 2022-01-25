package com.vabansal.api

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import com.vabansal.api.routes.CampaignRoutes
import com.vabansal.common.actor.CampaignActor
import com.vabansal.common.config.AppConfig

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}

object MainApiApp {

  private def startServer(routes: Route, config: AppConfig)(implicit system: ActorSystem[Nothing]): Unit = {

    implicit val executionContext: ExecutionContextExecutor = system.executionContext

    val host = config.api.server.host
    val port = config.api.server.port

    val serverBinding = Http().newServerAt(host, port).bind(routes)
    serverBinding.onComplete {
      case Success(binding) =>
        system.log.info(s"real-time-bidding-agent started at ${binding.localAddress}")
        sys.addShutdownHook {
          binding
            .terminate(hardDeadline = config.api.server.shutdownDeadLine)
            .onComplete { _ =>
              system.terminate()
              system.log.info("Termination completed")
            }
          system.log.info("Received termination signal")
        }

      case Failure(exception) =>
        system.log.error(
          s"Not able to start real-time-bidding-agent server at $host:$port  ${exception.getMessage}",
          exception,
        )
        system.terminate()
    }
  }

  def main(args: Array[String]): Unit = {

    val rootBehaviour = Behaviors.setup[Nothing] { context =>
      val config: AppConfig    = AppConfig()
      val campaignBiddingActor = context.spawn(CampaignActor(), "CampaignBiddingActor")
      context.watch(campaignBiddingActor)
      val routes = new CampaignRoutes(campaignBiddingActor, config)(context.system)
      startServer(routes.bidRoutes, config)(context.system)

      Behaviors.same
    }

    val system = ActorSystem[Nothing](rootBehaviour, "real-time-bidding-agent")

  }

}
