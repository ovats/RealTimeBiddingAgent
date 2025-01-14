package com.vabansal.api.routes

import akka.actor.typed.scaladsl.AskPattern.{Askable, _}
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.vabansal.common.domain.Domain.BidRequest
import com.vabansal.common.json.JsonFormats._
import com.vabansal.common.actor.CampaignActor
import com.vabansal.common.actor.CampaignActor.GetMatchingBidCampaign
import com.vabansal.common.config.AppConfig
import org.slf4j.LoggerFactory

import scala.concurrent.Future

class CampaignRoutes(campaignBiddingActor: ActorRef[CampaignActor.Command], config: AppConfig)(implicit
    val system: ActorSystem[_]
) {

  private implicit val timeout = Timeout(config.api.routes.askTimeout)

  private val logger = LoggerFactory.getLogger(getClass.getName)

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
