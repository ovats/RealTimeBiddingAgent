package com.vabansal.realTimeBiddingAgent.service


import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import akka.event.slf4j.Logger
import com.vabansal.realTimeBiddingAgent.domain.Domain.{BidRequest, RouteResponse}
import com.vabansal.realTimeBiddingAgent.repository.CampaignRepository
import org.slf4j.LoggerFactory



object CampaignBiddingService {

  val logger = Logger.apply(CampaignBiddingService.getClass , "")

  sealed trait Command

  final case class GetMatchingBidCampaign(bidRequest: BidRequest, replyTo: ActorRef[RouteResponse]) extends Command

  def apply(): Behavior[Command] = Behaviors.receiveMessage {

    case GetMatchingBidCampaign(bidRequest, replyTo) =>
      logger.info(s"Received Get Matching Bid Request ${bidRequest}")
      replyTo ! CampaignRepository.validateAndGetMatchingBidCampaign(bidRequest)
      Behaviors.same
  }
}
