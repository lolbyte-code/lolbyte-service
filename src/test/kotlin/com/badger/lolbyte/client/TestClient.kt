package com.badger.lolbyte.client

import com.badger.lolbyte.current.CurrentGameResponse
import com.badger.lolbyte.match.MatchResponse
import com.badger.lolbyte.rank.RankResponse
import com.badger.lolbyte.recent.RecentGameResponse
import com.badger.lolbyte.statistics.TopChampsResponse
import com.badger.lolbyte.summoner.SummonerResponse
import com.badger.lolbyte.utils.Region
import org.junit.jupiter.api.Assertions

open class TestClient(
    private val summonerResponse: SummonerResponse? = null,
    private val recentGamesResponse: List<RecentGameResponse>? = null,
    private val ranksResponse: List<RankResponse>? = null,
    private val topChampsResponse: TopChampsResponse? = null,
    private val champMapping: Map<Int, String>? = null,
    private val currentGameResponse: CurrentGameResponse? = null,
    private val matchResponse: MatchResponse? = null,
) : LeagueApiClient {
    override fun setRegion(region: Region) {}

    override fun getSummoner(name: String): SummonerResponse {
        return summonerResponse ?: Assertions.fail("No summonerResponse provided")
    }

    override fun getSummonerById(id: String): SummonerResponse {
        return summonerResponse ?: Assertions.fail("No summonerResponse provided")
    }

    override fun getRecentGames(id: String, limit: Int, queueId: Int?): List<RecentGameResponse> {
        return recentGamesResponse ?: Assertions.fail("No recentGamesResponse provided")
    }

    override fun getRanks(id: String): List<RankResponse> {
        return ranksResponse ?: Assertions.fail("No ranksResponse provided")
    }

    override fun getTopChamps(id: String, limit: Int): TopChampsResponse {
        return topChampsResponse ?: Assertions.fail("No topChampsResponse provided")
    }

    override fun getChampName(id: Int): String {
        return champMapping?.get(id) ?: Assertions.fail("No champMapping provided for $id")
    }

    override fun getCurrentGame(id: String, useRiotIds: Boolean): CurrentGameResponse {
        return currentGameResponse ?: Assertions.fail("No currentGameResponse provided")
    }

    override fun getMatch(id: Long, summonerId: String, useRiotIds: Boolean): MatchResponse {
        return matchResponse ?: Assertions.fail("No matchResponse provided")
    }
}
