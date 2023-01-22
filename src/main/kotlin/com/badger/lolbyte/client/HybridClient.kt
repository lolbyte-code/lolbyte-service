package com.badger.lolbyte.client

import com.badger.lolbyte.current.CurrentGameResponse
import com.badger.lolbyte.match.MatchResponse
import com.badger.lolbyte.rank.RankResponse
import com.badger.lolbyte.recent.RecentGameResponse
import com.badger.lolbyte.statistics.TopChampResponse
import com.badger.lolbyte.statistics.TopChampsResponse
import com.badger.lolbyte.summoner.SummonerResponse
import com.badger.lolbyte.utils.Region

class HybridClient(leagueApiKey: String, tftApiKey: String) : LeagueApiClient, TFTApiClient {
    private val oriannaClient = OriannaClient(leagueApiKey)
    private val r4JClient = R4JClient(leagueApiKey, tftApiKey)

    override fun setRegion(region: Region) {
        oriannaClient.setRegion(region)
        r4JClient.setRegion(region)
    }

    override fun getSummoner(name: String): SummonerResponse {
        return oriannaClient.getSummoner(name)
    }

    override fun getSummonerById(id: String): SummonerResponse {
        return oriannaClient.getSummonerById(id)
    }

    override fun getRecentGames(id: String, limit: Int, queueId: Int?): List<RecentGameResponse> {
        return r4JClient.getRecentGames(id, limit, queueId)
    }

    override fun getRanks(id: String): List<RankResponse> {
        val leagueRanks = r4JClient.getRanks(id)
        // Ids are specific to API key
        val name = getSummonerById(id).name
        val tftRanks = getTFTRanks(name)
        return if (leagueRanks.first().tier == "unranked" && tftRanks.isNotEmpty()) {
            tftRanks
        } else {
            (leagueRanks + tftRanks).sortedByDescending { it.score }
        }
    }

    override fun getTFTRanks(name: String): List<RankResponse> {
        return r4JClient.getTFTRanks(name)
    }

    override fun getTopChamps(id: String, limit: Int): TopChampsResponse {
        val topChampsResponse = r4JClient.getTopChamps(id, limit)
        return TopChampsResponse(
            topChampsResponse.champs.map { topChampResponse ->
                TopChampResponse(
                    topChampResponse.id,
                    getChampName(topChampResponse.id),
                    topChampResponse.level,
                    topChampResponse.points,
                )
            }
        )
    }

    override fun getChampName(id: Int): String {
        return oriannaClient.getChampName(id)
    }

    override fun getCurrentGame(id: String): CurrentGameResponse {
        return oriannaClient.getCurrentGame(id)
    }

    override fun getMatch(id: Long, summonerId: String): MatchResponse {
        return r4JClient.getMatch(id, summonerId)
    }
}
