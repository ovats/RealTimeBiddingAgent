package com.vabansal.common.actor

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import akka.event.slf4j.Logger
import com.vabansal.common.db.CampaignRepository
import com.vabansal.common.domain.Domain.{BidRequest, RouteResponse}
import org.slf4j.LoggerFactory

object CampaignActor {

  private val logger = Logger.apply(CampaignActor.getClass, "")

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
