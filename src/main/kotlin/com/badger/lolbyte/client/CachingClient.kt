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
    val summonerById = Cache.buildCache<String, SummonerResponse>(cacheProperties.summonerTtl)
    val recentGames = Cache.buildCache<String, List<RecentGameResponse>>(cacheProperties.recentGamesTtl)
    val ranks = Cache.buildCache<String, List<RankResponse>>(cacheProperties.ranksTtl)
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

    override fun getSummonerById(id: String): SummonerResponse {
        return summonerById.computeIfAbsent(id) {
            client.getSummoner(id)
        }
    }

    override fun getRecentGames(id: String, limit: Int, queueId: Int?): List<RecentGameResponse> {
        return recentGames.computeIfAbsent("$id$limit$queueId") {
            client.getRecentGames(id, limit, queueId)
        }
    }

    override fun getRanks(id: String): List<RankResponse> {
        return ranks.computeIfAbsent(id) {
            client.getRanks(id)
        }
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

    override fun getCurrentGame(id: String, useRiotIds: Boolean): CurrentGameResponse {
        return client.getCurrentGame(id, useRiotIds)
    }

    override fun getMatch(id: Long, summonerId: String, useRiotIds: Boolean): MatchResponse {
        return match.computeIfAbsent("$id$summonerId") {
            client.getMatch(id, summonerId, useRiotIds)
        }
    }
}

fun LeagueApiClient.withCaching(cacheProperties: CacheProperties): LeagueApiClient {
    return CachingClient(this, cacheProperties)
}
