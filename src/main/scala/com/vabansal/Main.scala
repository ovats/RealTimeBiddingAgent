package com.vabansal

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import com.vabansal.realTimeBiddingAgent.controller.CampaignRoutes
import com.vabansal.realTimeBiddingAgent.service.CampaignBiddingService

import scala.util.{Failure, Success}

object Main {


  private def startServer(routes: Route)(implicit actorSystem: ActorSystem[_]): Unit = {
    import actorSystem.executionContext

    val host = actorSystem.settings.config.getString("real-time-bidding-agent.server.host")
    val port = actorSystem.settings.config.getInt("real-time-bidding-agent.server.port")

    val serverBinding = Http().newServerAt(host, port).bind(routes)
    serverBinding.onComplete {
      case Success(binding) =>
        actorSystem.log.info(s"real-time-bidding-agent started at ${binding.localAddress}")
      case Failure(exception) =>
        actorSystem.log.error(s"Not able to start real-time-bidding-agent server at ${host}:${port}  ${exception.getMessage}")
        actorSystem.terminate()
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
