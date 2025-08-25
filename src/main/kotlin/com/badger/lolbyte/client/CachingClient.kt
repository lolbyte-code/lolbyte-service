package com.badger.lolbyte.client

import com.badger.lolbyte.config.CacheProperties
import com.badger.lolbyte.current.CurrentGameResponse
import com.badger.lolbyte.match.MatchResponse
import com.badger.lolbyte.rank.RankResponse
import com.badger.lolbyte.recent.RecentGameResponse
import com.badger.lolbyte.statistics.TopChampsResponse
import com.badger.lolbyte.summoner.SummonerResponse
import com.badger.lolbyte.utils.Cache
import com.badger.lolbyte.utils.Region

private class CachingClient(
    private val client: LeagueApiClient,
    cacheProperties: CacheProperties
) : LeagueApiClient {
    val summonerByName = Cache.buildCache<String, SummonerResponse>(cacheProperties.summonerTtl)
    val recentGames = Cache.buildCache<String, List<RecentGameResponse>>(cacheProperties.recentGamesTtl)
    val topChamps = Cache.buildCache<String, TopChampsResponse>(cacheProperties.topChampsTtl)
    val champById = Cache.buildCache<Int, String>(cacheProperties.champTtl)
    val match = Cache.buildCache<String, MatchResponse>(cacheProperties.matchTtl)

    override fun setRegion(region: Region) {
        client.setRegion(region)
    }

    override fun getSummoner(name: String): SummonerResponse {
        return summonerByName.computeIfAbsent(name) {
            client.getSummoner(name)
        }
    }

    override fun getRecentGames(id: String, limit: Int, queueId: Int?): List<RecentGameResponse> {
        return recentGames.computeIfAbsent("$id$limit$queueId") {
            client.getRecentGames(id, limit, queueId)
        }
    }

    override fun getRanks(id: String): List<RankResponse> {
        return client.getRanks(id)
    }

    override fun getTopChamps(id: String, limit: Int): TopChampsResponse {
        return topChamps.computeIfAbsent("$id$limit") {
            client.getTopChamps(id, limit)
        }
    }

    override fun getChampName(id: Int): String {
        return champById.computeIfAbsent(id) {
            client.getChampName(id)
        }
    }

    override fun getCurrentGame(id: String): CurrentGameResponse {
        return client.getCurrentGame(id)
    }

    override fun getMatch(id: Long): MatchResponse {
        return match.computeIfAbsent("$id") {
            client.getMatch(id)
        }
    }
}

fun LeagueApiClient.withCaching(cacheProperties: CacheProperties): LeagueApiClient {
    return CachingClient(this, cacheProperties)
}
