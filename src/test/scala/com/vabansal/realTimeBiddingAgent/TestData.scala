package com.vabansal.realTimeBiddingAgent

import com.vabansal.common.domain.Domain.{BidRequest, Device, Geo, Impression, Site, User}

object TestData {
  val validBidRequest: BidRequest = BidRequest(
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

  val validBidRequest2: BidRequest = BidRequest(
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

  val invalidBidRequest: BidRequest = BidRequest(
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

}
