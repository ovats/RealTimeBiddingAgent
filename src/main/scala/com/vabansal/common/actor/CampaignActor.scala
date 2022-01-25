package com.vabansal.common.actor

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import com.vabansal.api.routes.RouteResponse
import com.vabansal.common.db.CampaignRepository
import com.vabansal.common.domain.Domain.BidRequest
import org.slf4j.LoggerFactory

object CampaignActor {

  private val logger = LoggerFactory.getLogger(getClass.getName)

  sealed trait Command

  final case class GetMatchingBidCampaign(bidRequest: BidRequest, replyTo: ActorRef[RouteResponse]) extends Command

  def apply(): Behavior[Command] =
    Behaviors.receiveMessage {

      case GetMatchingBidCampaign(bidRequest, replyTo) =>
        logger.info(s"Received Get Matching Bid Request $bidRequest")
        replyTo ! CampaignRepository.validateAndGetMatchingBidCampaign(bidRequest)
        Behaviors.same
    }
}
