package com.badger.lolbyte.client

import com.badger.lolbyte.current.CurrentGameResponse
import com.badger.lolbyte.match.MatchResponse
import com.badger.lolbyte.rank.RankResponse
import com.badger.lolbyte.recent.RecentGameResponse
import com.badger.lolbyte.statistics.TopChampsResponse
import com.badger.lolbyte.summoner.SummonerResponse

interface RiotApiClient {
    fun getSummoner(name: String): SummonerResponse
    fun getRecentGames(id: String, limit: Int): List<RecentGameResponse>
    fun getRanks(id: String): List<RankResponse>
    fun getTopChamps(id: String, limit: Int): TopChampsResponse
    fun getChampName(id: Int): String
    fun getCurrentGame(id: String): CurrentGameResponse
    fun getMatch(id: Long, summonerId: String): MatchResponse
}
