package com.vabansal.common.db.dummydata

import com.vabansal.common.domain.Domain.{Banner, Campaign, Targeting}

object DummyData {
  // format: off
  val activeCampaigns = Seq(
    Campaign(id = 1, country = "LT", targeting = Targeting(targetedSiteIds = List("0006a522ce0f4bbbbaa6b3c38cafaa0f")),
      banners = List(
        Banner(id = 1, src = "https://business.eskimi.com/wp-content/uploads/2020/06/openGraph.jpeg", width = 300, height = 250),
        Banner(id = 2, src = "https://business.eskimi.com/wp-content/uploads/2020/06/openGraph1.jpeg", width = 300, height = 250),
        Banner(id = 3, src = "https://business.eskimi.com/wp-content/uploads/2020/06/openGraph2.jpeg", width = 300, height = 250),
        Banner(id = 4, src = "https://business.eskimi.com/wp-content/uploads/2020/06/openGraph3.jpeg", width = 3000, height = 2500)
      ),
      bid = 4d
    ),
    Campaign(id = 1000, country = "CHN", targeting = Targeting(targetedSiteIds = List("0006a522ce0f4bbbbaa6b3c38cafaa0f")),
      banners = List(Banner(id = 2, src = "https://business.eskimi.com/wp-content/uploads/2020/06/CHN.jpeg", width = 300, height = 250)
      ),bid = 8d),
    Campaign(id = 2000, country = "UK", targeting = Targeting(targetedSiteIds = List("0006a522ce0f4bbbbaa6b3c38cafaa0f")),
      banners = List(Banner(id = 3, src = "https://business.eskimi.com/wp-content/uploads/2020/06/UK.jpeg", width = 300, height = 250)
      ),bid = 9d),
    Campaign(id = 1500, country = "IN", targeting = Targeting(targetedSiteIds = List("0006a522ce0f4bbbbaa6b3c38cafaa0f")),
      banners = List(Banner(id = 1, src = "https://business.eskimi.com/wp-content/uploads/2020/06/openGraph.jpeg", width = 1300, height = 250)),
      bid = 8d),
    Campaign(id = 3, country = "NEP", targeting = Targeting(targetedSiteIds = List("0006a522ce0f4bbbbaa6b3c38cafaa0f")),
      banners = List(Banner(id = 1, src = "https://business.eskimi.com/wp-content/uploads/2020/06/openGraph.jpeg", width = 3000, height = 2500)),
      bid = 2d
    ),
    Campaign(id = 2001, country = "US", targeting = Targeting(targetedSiteIds = List("0006a522ce0f4bbbbaa6b3c38cafaa0f")),
      banners = List(Banner(id = 1, src = "https://business.eskimi.com/wp-content/uploads/2020/06/openGraph.jpeg", width = 3000, height = 2500)),
      bid = 1d
    )
  )
  // format: on

}
