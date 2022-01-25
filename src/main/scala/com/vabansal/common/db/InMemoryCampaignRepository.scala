package com.vabansal.common.db

import com.vabansal.common.domain.Domain.Campaign

import scala.collection.concurrent.TrieMap

class InMemoryCampaignRepository extends Repository[Int, Campaign] {

  private val repo: TrieMap[Int, Campaign] = TrieMap.empty

  override def create(entity: Campaign): Campaign = {
    repo += (entity.id -> entity)
    entity
  }

  override def read(key: Int): Option[Campaign] = {
    repo.get(key)
  }

  override def update(entity: Campaign): Campaign = {
    repo += (entity.id -> entity)
    entity
  }

  override def delete(key: Int): Option[Campaign] = {
    repo.remove(key)
  }

  override def findAll(): Seq[Campaign] = {
    repo.toList.map(_._2)
  }

}
