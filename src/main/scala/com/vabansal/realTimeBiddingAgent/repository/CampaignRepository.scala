package com.vabansal.realTimeBiddingAgent.repository

import akka.event.slf4j.Logger
import akka.http.scaladsl.model.StatusCodes
import com.vabansal.realTimeBiddingAgent.domain.Domain._

object CampaignRepository {
  val logger = Logger.apply(CampaignRepository.getClass, "")

  val activeCampaigns = Seq(
    Campaign(id = 1, country = "LT", targeting = Targeting(targetedSiteIds = List("0006a522ce0f4bbbbaa6b3c38cafaa0f")
    ), banners = List(Banner(id = 1, src = "https://business.eskimi.com/wp-content/uploads/2020/06/openGraph.jpeg", width = 300, height = 250),
      Banner(id = 2, src = "https://business.eskimi.com/wp-content/uploads/2020/06/openGraph1.jpeg", width = 300, height = 250)
      , Banner(id = 3, src = "https://business.eskimi.com/wp-content/uploads/2020/06/openGraph2.jpeg", width = 300, height = 250),
      Banner(id = 4, src = "https://business.eskimi.com/wp-content/uploads/2020/06/openGraph3.jpeg", width = 3000, height = 2500)
    ),
      bid = 4d
    ),
    Campaign(id = 1000, country = "CHN", targeting = Targeting(targetedSiteIds = List("0006a522ce0f4bbbbaa6b3c38cafaa0f")
    ), banners = List(Banner(id = 2, src = "https://business.eskimi.com/wp-content/uploads/2020/06/CHN.jpeg", width = 300, height = 250)
    ),
      bid = 8d
    ),
    Campaign(id = 2000, country = "UK", targeting = Targeting(targetedSiteIds = List("0006a522ce0f4bbbbaa6b3c38cafaa0f")
    ), banners = List(Banner(id = 3, src = "https://business.eskimi.com/wp-content/uploads/2020/06/UK.jpeg", width = 300, height = 250)
    ),
      bid = 9d
    ),
    Campaign(id = 1500, country = "IN", targeting = Targeting(targetedSiteIds = List("0006a522ce0f4bbbbaa6b3c38cafaa0f")
    ), banners = List(Banner(id = 1, src = "https://business.eskimi.com/wp-content/uploads/2020/06/openGraph.jpeg", width = 1300, height = 250)), bid = 8d
    ),
    Campaign(id = 3, country = "NEP", targeting = Targeting(targetedSiteIds = List("0006a522ce0f4bbbbaa6b3c38cafaa0f")
      ), banners = List(Banner(id = 1, src = "https://business.eskimi.com/wp-content/uploads/2020/06/openGraph.jpeg", width = 3000, height = 2500
        )
      ),
      bid = 2d
    ),
    Campaign(id = 2001, country = "US", targeting = Targeting(targetedSiteIds = List("0006a522ce0f4bbbbaa6b3c38cafaa0f")
      ), banners = List(Banner(id = 1, src = "https://business.eskimi.com/wp-content/uploads/2020/06/openGraph.jpeg", width = 3000, height = 2500)),
      bid = 1d
    )
  )

  def validateAndGetMatchingBidCampaign(bidRequest: BidRequest): RouteResponse = {
    try {
      if (bidRequest.imp.isEmpty || bidRequest.imp.get.isEmpty || (bidRequest.user.isEmpty && bidRequest.device.isEmpty)) {
        createEmptyCampaignResponse()
      } else findMatchingBidCampaigns(bidRequest)
    } catch {
      case ex: Throwable =>
        logger.error(s"Exception in validating Matching Bid ${bidRequest.id} ${ex.getMessage}")
        createEmptyCampaignResponse()
    }

  }


  private def findMatchingBidCampaigns(bidRequest: BidRequest): RouteResponse = {
    val userGeo = bidRequest.user.flatMap(_.geo.flatMap(_.country))
    val deviceGeo = bidRequest.user.flatMap(_.geo.flatMap(_.country))
    val matchingCampaigns = activeCampaigns.filter(_.targeting.targetedSiteIds.contains(bidRequest.site.id))
      .filter(_.bid >= bidRequest.imp.get.head.bidFloor.getOrElse(0.0))
      .map(x => x.copy(banners = x.banners.filter(bnr => bannerFilter(bnr, bidRequest))))

    val headMatchingCampaign = matchingCampaigns.find(cmpn => deviceGeo.contains(cmpn.country) && cmpn.banners.nonEmpty) match {
      case Some(campaign) => Some(campaign)
      case _ => matchingCampaigns.find(cmpn => userGeo.contains(cmpn.country) && cmpn.banners.nonEmpty)
    }
    createRouteResponse(headMatchingCampaign, bidRequest)

  }

  private def createEmptyCampaignResponse(): RouteResponse = {
    RouteResponse(StatusCodes.NoContent, None)

  }

  private def createBidResponseId(campaign: Option[Campaign], bidRequest: BidRequest): String = {
    campaign.map(cmpn => (cmpn.id.hashCode() + cmpn.bid.hashCode() + bidRequest.site.domain.hashCode).toString).getOrElse("-1")
  }

  private def createRouteResponse(campaign: Option[Campaign], bidRequest: BidRequest): RouteResponse = {
    campaign match {
      case Some(cmpn) =>
        val bidResponse = BidResponse(createBidResponseId(campaign, bidRequest), bidRequest.id, bidRequest.imp.flatMap(_.head.bidFloor).getOrElse(0.0), Some(cmpn.id.toString), cmpn.banners.headOption)
        RouteResponse(StatusCodes.OK, Some(bidResponse))
      case _ => createEmptyCampaignResponse()
    }
  }

  private def bannerFilter(banner: Banner, bidRequest: BidRequest): Boolean = {
    val isHeightDefined = bidRequest.imp.get.head.h.nonEmpty
    val isWidthDefined = bidRequest.imp.get.head.w.nonEmpty

    (isHeightDefined, isWidthDefined) match {
      case (true, true) => bidRequest.imp.get.head.h.contains(banner.height) && bidRequest.imp.get.head.w.contains(banner.width)
      case (false, true) => bidRequest.imp.get.head.hmax.exists(_.>=(banner.height)) && bidRequest.imp.get.head.hmin.exists(_.<=(banner.height)) && bidRequest.imp.get.head.w.contains(banner.width)
      case (true, false) => bidRequest.imp.get.head.wmax.exists(_.>=(banner.width)) && bidRequest.imp.get.head.wmin.exists(_.<=(banner.width)) && bidRequest.imp.get.head.h.contains(banner.height)
      case (false, false) => bidRequest.imp.get.head.hmax.exists(_.>=(banner.height)) && bidRequest.imp.get.head.hmin.exists(_.<=(banner.height)) && bidRequest.imp.get.head.wmax.exists(_.>=(banner.width)) && bidRequest.imp.get.head.wmin.exists(_.<=(banner.width))
    }
  }


}
