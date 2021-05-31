package com.badger.lolbyte.client

import com.badger.lolbyte.rank.RankResponse
import com.badger.lolbyte.recent.RecentGameResponse
import com.badger.lolbyte.summoner.SummonerResponse

interface RiotApiClient {
    fun getSummoner(name: String): SummonerResponse
    fun getRecentGames(id: String, limit: Int): List<RecentGameResponse>
    fun getRanks(id: String): List<RankResponse>
}
