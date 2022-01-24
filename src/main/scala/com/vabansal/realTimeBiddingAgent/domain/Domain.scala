package com.vabansal.realTimeBiddingAgent.domain

//TODO this is not part of a domain
import akka.http.scaladsl.model.StatusCode

object Domain {
  case class Campaign(id: Int, country: String, targeting: Targeting, banners: List[Banner], bid: Double)

  case class Targeting(targetedSiteIds: List[String])

  case class Banner(id: Int, src: String, width: Int, height: Int)

  case class BidRequest(
      id: String,
      imp: Option[List[Impression]],
      site: Site,
      user: Option[User],
      device: Option[Device],
  )

  case class Impression(
      id: String,
      wmin: Option[Int],
      wmax: Option[Int],
      w: Option[Int],
      hmin: Option[Int],
      hmax: Option[Int],
      h: Option[Int],
      bidFloor: Option[Double],
  )

  case class Site(id: String, domain: String)

  case class User(id: String, geo: Option[Geo])

  case class Device(id: String, geo: Option[Geo])

  case class Geo(country: Option[String])

  case class BidResponse(id: String, bidRequestId: String, price: Double, adid: Option[String], banner: Option[Banner])

  //TODO this is not part of a domain
  case class RouteResponse(status: StatusCode, response: Option[BidResponse])

}
