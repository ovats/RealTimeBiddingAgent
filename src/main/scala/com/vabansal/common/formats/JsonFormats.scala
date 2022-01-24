package com.vabansal.common.formats

import com.vabansal.common.domain.Domain
import spray.json.DefaultJsonProtocol

object JsonFormats {
  import DefaultJsonProtocol._

  implicit val targetingJsonFormat   = jsonFormat1(Domain.Targeting)
  implicit val bannerJsonFormat      = jsonFormat4(Domain.Banner)
  implicit val campaignJsonFormat    = jsonFormat5(Domain.Campaign)
  implicit val geoJsonFormat         = jsonFormat1(Domain.Geo)
  implicit val deviceJsonFormat      = jsonFormat2(Domain.Device)
  implicit val siteJsonFormat        = jsonFormat2(Domain.Site)
  implicit val userJsonFormat        = jsonFormat2(Domain.User)
  implicit val impressionJsonFormat  = jsonFormat8(Domain.Impression)
  implicit val bidRequestJsonFormat  = jsonFormat5(Domain.BidRequest)
  implicit val bidResponseJsonFormat = jsonFormat5(Domain.BidResponse)

}
