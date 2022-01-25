package com.vabansal.api.routes

import akka.http.scaladsl.model.StatusCode
import com.vabansal.common.domain.Domain.BidResponse

case class RouteResponse(status: StatusCode, response: Option[BidResponse])
