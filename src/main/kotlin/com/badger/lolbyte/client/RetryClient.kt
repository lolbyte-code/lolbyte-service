package com.badger.lolbyte.client

import com.badger.lolbyte.current.CurrentGameResponse
import com.badger.lolbyte.match.MatchResponse
import com.badger.lolbyte.rank.RankResponse
import com.badger.lolbyte.recent.RecentGameResponse
import com.badger.lolbyte.statistics.TopChampsResponse
import com.badger.lolbyte.summoner.SummonerResponse
import com.badger.lolbyte.utils.Region
import com.badger.lolbyte.utils.Retrier
import java.lang.NullPointerException

class RetryClient(private val client: AllApiClient, private val retrier: Retrier) : AllApiClient {
    override fun setRegion(region: Region) = client.setRegion(region)
    override fun getSummoner(name: String): SummonerResponse = retrier.withRetry { client.getSummoner(name) }
    override fun getSummonerById(id: String): SummonerResponse = retrier.withRetry { client.getSummonerById(id) }
    override fun getRecentGames(id: String, limit: Int, queueId: Int?): List<RecentGameResponse> {
        return try {
            retrier.withRetry { client.getRecentGames(id, limit, queueId) }
        } catch (e: NullPointerException) {
            // If this _still_ returns an NPE, just return empty list.
            emptyList()
        }
    }
    override fun getRanks(id: String): List<RankResponse> {
        return try {
            retrier.withRetry { client.getRanks(id) }
        } catch (e: NullPointerException) {
            // If this _still_ returns an NPE, just return empty list.
            emptyList()
        }
    }
    override fun getTopChamps(id: String, limit: Int): TopChampsResponse = retrier.withRetry { client.getTopChamps(id, limit) }
    override fun getChampName(id: Int): String = retrier.withRetry { client.getChampName(id) }
    override fun getCurrentGame(id: String): CurrentGameResponse = retrier.withRetry { client.getCurrentGame(id) }
    override fun getMatch(id: Long, summonerId: String): MatchResponse = retrier.withRetry { client.getMatch(id, summonerId) }
    override fun getTFTRanks(name: String): List<RankResponse> = retrier.withRetry { client.getTFTRanks(name) }
}
