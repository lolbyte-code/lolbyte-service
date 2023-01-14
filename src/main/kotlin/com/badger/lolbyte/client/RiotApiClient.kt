package com.badger.lolbyte.client

import com.badger.lolbyte.current.CurrentGameResponse
import com.badger.lolbyte.match.MatchResponse
import com.badger.lolbyte.rank.RankResponse
import com.badger.lolbyte.recent.RecentGameResponse
import com.badger.lolbyte.statistics.TopChampsResponse
import com.badger.lolbyte.summoner.SummonerResponse
import com.badger.lolbyte.utils.Region

interface RiotApiClient {
    fun setRegion(region: Region)
    fun getSummoner(name: String): SummonerResponse
    fun getRecentGames(id: String, limit: Int, queueId: Int?): List<RecentGameResponse>
    fun getRanks(id: String): List<RankResponse>
    fun getTopChamps(id: String, limit: Int): TopChampsResponse
    fun getChampName(id: Int): String
    fun getCurrentGame(id: String): CurrentGameResponse
    fun getMatch(id: Long, summonerId: String): MatchResponse
    fun getTFTRanks(id: String): List<RankResponse>
}
