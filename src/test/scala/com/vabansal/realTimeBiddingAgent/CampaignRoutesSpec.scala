package com.vabansal.realTimeBiddingAgent

import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.{ContentTypes, MessageEntity, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.vabansal.api.routes.CampaignRoutes
import com.vabansal.realTimeBiddingAgent.domain.Domain._
import com.vabansal.realTimeBiddingAgent.service.CampaignBiddingService
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class CampaignRoutesSpec extends AnyWordSpec with Matchers with ScalaFutures with ScalatestRouteTest {

  private lazy val testKit = ActorTestKit()

  private implicit def typedSystem = testKit.system

  override def createActorSystem(): akka.actor.ActorSystem = testKit.system.classicSystem

  //TODO move imports to the top
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import com.vabansal.realTimeBiddingAgent.domain.JsonFormats._

  private val campaignRegistry = testKit.spawn(CampaignBiddingService())
  private lazy val routes      = new CampaignRoutes(campaignRegistry).bidRoutes

  private val validBidRequest = BidRequest(
    id = "SGu1Jpq1IO",
    site = Site(
      id = "0006a522ce0f4bbbbaa6b3c38cafaa0f",
      domain = "fake.tld",
    ),
    device = Some(
      Device(
        id = "440579f4b408831516ebd02f6e1c31b4",
        geo = Some(
          Geo(
            country = Some("IN")
          )
        ),
      )
    ),
    imp = Some(
      List(
        Impression(
          id = "1",
          wmin = Some(50),
          wmax = Some(300),
          hmin = Some(100),
          hmax = Some(300),
          h = Some(250),
          w = Some(300),
          bidFloor = Some(3.12123),
        )
      )
    ),
    user = Some(
      User(
        geo = Some(
          Geo(
            country = Some("IN")
          )
        ),
        id = "USARIO1",
      )
    ),
  )

  private val validBidRequest2 = BidRequest(
    id = "SGu1Jpq1IO",
    site = Site(
      id = "0006a522ce0f4bbbbaa6b3c38cafaa0f",
      domain = "fake.tld",
    ),
    device = Some(
      Device(
        id = "440579f4b408831516ebd02f6e1c31b4",
        geo = Some(
          Geo(
            country = Some("UK")
          )
        ),
      )
    ),
    imp = Some(
      List(
        Impression(
          id = "1",
          wmin = Some(50),
          wmax = Some(300),
          hmin = Some(100),
          hmax = Some(300),
          h = Some(250),
          w = Some(300),
          bidFloor = Some(3.12123),
        )
      )
    ),
    user = Some(
      User(
        geo = Some(
          Geo(
            country = Some("CHN")
          )
        ),
        id = "USARIO1",
      )
    ),
  )

  private val invalidBidRequest = BidRequest(
    id = "SGu1Jpq1IO",
    site = Site(
      id = "0006a522ce0f4bbbbaa6b3c38cafaa0f",
      domain = "fake.tld",
    ),
    device = Some(
      Device(
        id = "440579f4b408831516ebd02f6e1c31b4",
        geo = Some(
          Geo(
            country = Some("UK")
          )
        ),
      )
    ),
    imp = Some(List.empty),
    user = Some(
      User(
        geo = Some(
          Geo(
            country = Some("CHN")
          )
        ),
        id = "USARIO1",
      )
    ),
  )

  "be able to bid campaign successfully" in {
    val bidRequestEntity = Marshal(validBidRequest).to[MessageEntity].futureValue
    val request          = Post("/bidCampaign").withEntity(bidRequestEntity)

    request ~> routes ~> check {
      status should ===(StatusCodes.OK)

      // we expect the response to be json:
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
