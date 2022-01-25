package com.vabansal.realTimeBiddingAgent

import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.{ContentTypes, MessageEntity, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.vabansal.api.routes.CampaignRoutes
import com.vabansal.common.actor.CampaignActor
import com.vabansal.realTimeBiddingAgent.TestData.{invalidBidRequest, validBidRequest, validBidRequest2}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import com.vabansal.common.config.AppConfig
import com.vabansal.common.json.JsonFormats._

class CampaignRoutesSpec extends AnyWordSpec with Matchers with ScalaFutures with ScalatestRouteTest {

  private lazy val testKit         = ActorTestKit()
  private implicit def typedSystem = testKit.system
  private val campaignRegistry     = testKit.spawn(CampaignActor())
  private val config               = AppConfig()
  private lazy val routes          = new CampaignRoutes(campaignRegistry, config).bidRoutes

  override def createActorSystem(): akka.actor.ActorSystem = testKit.system.classicSystem

  "be able to bid campaign successfully" in {
    val bidRequestEntity = Marshal(validBidRequest).to[MessageEntity].futureValue
    val request          = Post("/bidCampaign").withEntity(bidRequestEntity)

    request ~> routes ~> check {
      status should ===(StatusCodes.OK)
      contentType should ===(ContentTypes.`application/json`)

      entityAs[String] should ===(
        """{"adid":"2","banner":{"height":250,"id":1,"src":"https://business.eskimi.com/wp-content/uploads/2020/06/openGraph.jpeg","width":300},"bidRequestId":"SGu1Jpq1IO","id":"1799671509","price":3.12123}"""
      )
    }

  }

  "device geo should get higher priority that user geo" in {
    val bidRequestEntity = Marshal(validBidRequest2).to[MessageEntity].futureValue
    val request          = Post("/bidCampaign").withEntity(bidRequestEntity)

    request ~> routes ~> check {
      status should ===(StatusCodes.OK)
      contentType should ===(ContentTypes.`application/json`)

      entityAs[String] should ===(
        """{"adid":"1000","banner":{"height":250,"id":2,"src":"https://business.eskimi.com/wp-content/uploads/2020/06/CHN.jpeg","width":300},"bidRequestId":"SGu1Jpq1IO","id":"1800458939","price":3.12123}"""
      )
    }

  }

  "must return status code 204 on receiving invalid bid request" in {
    val bidRequestEntity = Marshal(invalidBidRequest).to[MessageEntity].futureValue
    val request          = Post("/bidCampaign").withEntity(bidRequestEntity)

    request ~> routes ~> check {
      status should ===(StatusCodes.NoContent)
    }
  }

}
