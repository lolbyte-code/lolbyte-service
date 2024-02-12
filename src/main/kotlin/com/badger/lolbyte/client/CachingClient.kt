package com.badger.lolbyte.client

import com.badger.lolbyte.config.CacheProperties
import com.badger.lolbyte.current.CurrentGameResponse
import com.badger.lolbyte.match.MatchResponse
import com.badger.lolbyte.rank.RankResponse
import com.badger.lolbyte.recent.RecentGameResponse
import com.badger.lolbyte.statistics.TopChampsResponse
import com.badger.lolbyte.summoner.SummonerResponse
import com.badger.lolbyte.utils.Region
import org.cache2k.Cache
import org.cache2k.Cache2kBuilder
import java.util.concurrent.TimeUnit

private class CachingClient(
    private val client: LeagueApiClient,
    cacheProperties: CacheProperties
) : LeagueApiClient {
    companion object {
        inline fun <reified K, reified V> buildCache(expirationMinutes: Long): Cache<K, V> {
            val builder = Cache2kBuilder.of(K::class.java, V::class.java)
            if (expirationMinutes < 0) {
                builder.eternal(true)
            } else {
                builder.expireAfterWrite(expirationMinutes, TimeUnit.MINUTES)
            }
            return builder.build()
        }
    }

    val summonerByName = buildCache<String, SummonerResponse>(cacheProperties.summonerTtl)
    val summonerById = buildCache<String, SummonerResponse>(cacheProperties.summonerTtl)
    val recentGames = buildCache<String, List<RecentGameResponse>>(cacheProperties.recentGamesTtl)
    val ranks = buildCache<String, List<RankResponse>>(cacheProperties.ranksTtl)
    val topChamps = buildCache<String, TopChampsResponse>(cacheProperties.topChampsTtl)
    val champById = buildCache<Int, String>(cacheProperties.champTtl)
    val match = buildCache<String, MatchResponse>(cacheProperties.matchTtl)

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

    override fun getCurrentGame(id: String): CurrentGameResponse {
        return client.getCurrentGame(id)
    }

    override fun getMatch(id: Long, summonerId: String): MatchResponse {
        return match.computeIfAbsent("$id$summonerId") {
            client.getMatch(id, summonerId)
        }
    }
}

fun LeagueApiClient.withCaching(cacheProperties: CacheProperties): LeagueApiClient {
    return CachingClient(this, cacheProperties)
}
