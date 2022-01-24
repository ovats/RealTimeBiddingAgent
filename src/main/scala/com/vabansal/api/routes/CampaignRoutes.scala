package com.vabansal.api.routes

import akka.actor.typed.scaladsl.AskPattern.{Askable, _}
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.event.slf4j.Logger
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.vabansal.realTimeBiddingAgent.domain.Domain.{BidRequest, RouteResponse}
import com.vabansal.realTimeBiddingAgent.domain.JsonFormats._
import com.vabansal.realTimeBiddingAgent.service.CampaignBiddingService
import com.vabansal.realTimeBiddingAgent.service.CampaignBiddingService.GetMatchingBidCampaign

import scala.concurrent.Future

class CampaignRoutes(campaignBiddingActor: ActorRef[CampaignBiddingService.Command])(implicit
    val system: ActorSystem[_]
) {

  private implicit val timeout =
    Timeout.create(system.settings.config.getDuration("real-time-bidding-agent.routes.ask-timeout"))
  private val logger = Logger.apply(this.getClass, "")

  private def getMatchingBidCampaign(bidRequest: BidRequest): Future[RouteResponse] = {
    campaignBiddingActor.ask(GetMatchingBidCampaign(bidRequest, _))
  }

  val bidRoutes: Route = pathPrefix("bidCampaign") {
    concat(
      pathEnd {
        post {
          entity(as[BidRequest]) { bidRequest =>
            onSuccess(getMatchingBidCampaign(bidRequest)) { response =>
              logger.info(s"sending response $response")
              complete(response.status, response.response)
            }
          }
        }
      }
    )
  }

}
