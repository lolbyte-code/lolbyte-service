package com.badger.lolbyte.client

import com.badger.lolbyte.NotFoundException
import com.badger.lolbyte.config.RetryProperties
import com.badger.lolbyte.current.CurrentGameResponse
import com.badger.lolbyte.match.MatchResponse
import com.badger.lolbyte.rank.RankResponse
import com.badger.lolbyte.recent.RecentGameResponse
import com.badger.lolbyte.statistics.TopChampsResponse
import com.badger.lolbyte.summoner.SummonerResponse
import com.badger.lolbyte.utils.Region
import com.badger.lolbyte.utils.Retrier
import java.lang.NullPointerException

private class RetryClient(private val client: LeagueApiClient, private val retrier: Retrier) : LeagueApiClient {
    override fun setRegion(region: Region) = client.setRegion(region)
    override fun getSummoner(name: String): SummonerResponse {
        return try {
            retrier.withRetry { client.getSummoner(name) }
        } catch (e: NullPointerException) {
            throw NotFoundException
        }
    }
    override fun getSummonerById(id: String): SummonerResponse {
        return try {
            retrier.withRetry { client.getSummonerById(id) }
        } catch (e: NullPointerException) {
            throw NotFoundException
        }
    }
    override fun getRecentGames(id: String, limit: Int, queueId: Int?): List<RecentGameResponse> {
        return try {
            retrier.withRetry { client.getRecentGames(id, limit, queueId) }
        } catch (e: NullPointerException) {
            emptyList()
        }
    }
    override fun getRanks(id: String): List<RankResponse> {
        return try {
            retrier.withRetry { client.getRanks(id) }
        } catch (e: NullPointerException) {
            emptyList()
        }
    }
    override fun getTopChamps(id: String, limit: Int): TopChampsResponse {
        return try {
            retrier.withRetry { client.getTopChamps(id, limit) }
        } catch (e: NullPointerException) {
            TopChampsResponse(emptyList())
        }
    }
    override fun getChampName(id: Int): String {
        return try {
            retrier.withRetry { client.getChampName(id) }
        } catch (e: NullPointerException) {
            return "Unknown"
        }
    }
    override fun getCurrentGame(id: String): CurrentGameResponse {
        return try {
            retrier.withRetry { client.getCurrentGame(id) }
        } catch (e: NullPointerException) {
            throw NotFoundException
        }
    }
    override fun getMatch(id: Long, summonerId: String): MatchResponse = retrier.withRetry { client.getMatch(id, summonerId) }
}

fun LeagueApiClient.withRetry(retryProperties: RetryProperties): LeagueApiClient {
    val retrier = Retrier(retryProperties.intervalInSeconds, retryProperties.attempts)
    return RetryClient(this, retrier)
}
