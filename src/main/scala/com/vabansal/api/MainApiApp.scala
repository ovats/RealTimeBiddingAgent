package com.vabansal.api

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import com.vabansal.api.routes.CampaignRoutes
import com.vabansal.common.service.CampaignBiddingService

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object MainApiApp {

  private def startServer(routes: Route)(implicit system: ActorSystem[Nothing]): Unit = {

    implicit val executionContext: ExecutionContextExecutor = system.executionContext

    val host             = system.settings.config.getString("real-time-bidding-agent.server.host")
    val port             = system.settings.config.getInt("real-time-bidding-agent.server.port")
    val shutdownDeadline = system.settings.config.getLong("real-time-bidding-agent.server.shutdown-dead-line")

    val serverBinding = Http().newServerAt(host, port).bind(routes)
    serverBinding.onComplete {
      case Success(binding) =>
        system.log.info(s"real-time-bidding-agent started at ${binding.localAddress}")
        sys.addShutdownHook {
          binding
            .terminate(hardDeadline = shutdownDeadline.seconds)
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
      val campaignBiddingActor = context.spawn(CampaignBiddingService(), "CampaignBiddingActor")
      context.watch(campaignBiddingActor)
      val routes = new CampaignRoutes(campaignBiddingActor)(context.system)
      startServer(routes.bidRoutes)(context.system)

      Behaviors.same
    }

    val system = ActorSystem[Nothing](rootBehaviour, "real-time-bidding-agent")

  }

}
