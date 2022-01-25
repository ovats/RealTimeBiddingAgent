package com.vabansal.common.db

import akka.http.scaladsl.model.StatusCodes
import com.vabansal.api.routes.RouteResponse
import com.vabansal.common.db.dummydata.DummyData
import com.vabansal.common.domain.Domain._
import org.slf4j.LoggerFactory

object CampaignRepository {
  private val logger = LoggerFactory.getLogger(getClass.getName)

  //TODO this method is not part of a repository
  def validateAndGetMatchingBidCampaign(bidRequest: BidRequest): RouteResponse = {
    try {
      if (
        bidRequest.imp.isEmpty || bidRequest.imp.get.isEmpty || (bidRequest.user.isEmpty && bidRequest.device.isEmpty)
      ) {
        createEmptyCampaignResponse()
      } else findMatchingBidCampaigns(bidRequest)
    } catch {
      case ex: Throwable =>
        logger.error(s"Exception in validating Matching Bid ${bidRequest.id} ${ex.getMessage}")
        createEmptyCampaignResponse()
    }

  }

  private def findMatchingBidCampaigns(bidRequest: BidRequest): RouteResponse = {
    val userGeo   = bidRequest.user.flatMap(_.geo.flatMap(_.country))
    val deviceGeo = bidRequest.user.flatMap(_.geo.flatMap(_.country))
    val matchingCampaigns = DummyData.activeCampaigns
      .filter(_.targeting.targetedSiteIds.contains(bidRequest.site.id))
      .filter(_.bid >= bidRequest.imp.get.head.bidFloor.getOrElse(0.0))
      .map(x => x.copy(banners = x.banners.filter(bnr => bannerFilter(bnr, bidRequest))))

    val headMatchingCampaign =
      matchingCampaigns.find(cmpn => deviceGeo.contains(cmpn.country) && cmpn.banners.nonEmpty) match {
        case Some(campaign) => Some(campaign)
        case _              => matchingCampaigns.find(cmpn => userGeo.contains(cmpn.country) && cmpn.banners.nonEmpty)
      }
    createRouteResponse(headMatchingCampaign, bidRequest)

  }

  //TODO StatusCodes should not be part of a repository
  private def createEmptyCampaignResponse(): RouteResponse = {
    RouteResponse(StatusCodes.NoContent, None)
  }

  private def createBidResponseId(campaign: Option[Campaign], bidRequest: BidRequest): String = {
    campaign
      .map(cmpn => (cmpn.id.hashCode() + cmpn.bid.hashCode() + bidRequest.site.domain.hashCode).toString)
      .getOrElse("-1")
  }

  private def createRouteResponse(campaign: Option[Campaign], bidRequest: BidRequest): RouteResponse = {
    campaign match {
      case Some(cmpn) =>
        val bidResponse = BidResponse(
          createBidResponseId(campaign, bidRequest),
          bidRequest.id,
          bidRequest.imp.flatMap(_.head.bidFloor).getOrElse(0.0),
          Some(cmpn.id.toString),
          cmpn.banners.headOption,
        )
        RouteResponse(StatusCodes.OK, Some(bidResponse))
      case _ => createEmptyCampaignResponse()
    }
  }

  private def bannerFilter(banner: Banner, bidRequest: BidRequest): Boolean = {
    val isHeightDefined = bidRequest.imp.get.head.h.nonEmpty
    val isWidthDefined  = bidRequest.imp.get.head.w.nonEmpty

    (isHeightDefined, isWidthDefined) match {
      case (true, true) =>
        bidRequest.imp.get.head.h.contains(banner.height) && bidRequest.imp.get.head.w.contains(banner.width)
      case (false, true) =>
        bidRequest.imp.get.head.hmax.exists(_.>=(banner.height)) && bidRequest.imp.get.head.hmin
            .exists(_.<=(banner.height)) && bidRequest.imp.get.head.w.contains(banner.width)
      case (true, false) =>
        bidRequest.imp.get.head.wmax.exists(_.>=(banner.width)) && bidRequest.imp.get.head.wmin
            .exists(_.<=(banner.width)) && bidRequest.imp.get.head.h.contains(banner.height)
      case (false, false) =>
        bidRequest.imp.get.head.hmax.exists(_.>=(banner.height)) && bidRequest.imp.get.head.hmin
            .exists(_.<=(banner.height)) && bidRequest.imp.get.head.wmax
            .exists(_.>=(banner.width)) && bidRequest.imp.get.head.wmin.exists(_.<=(banner.width))
    }
  }

}
