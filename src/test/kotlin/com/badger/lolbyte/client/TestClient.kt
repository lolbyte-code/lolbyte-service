package com.badger.lolbyte.client

import com.badger.lolbyte.rank.RankResponse
import com.badger.lolbyte.recent.RecentGameResponse
import com.badger.lolbyte.summoner.SummonerResponse
import org.junit.jupiter.api.Assertions

class TestClient(
    private val summonerResponse: SummonerResponse? = null,
    private val recentGamesResponse: List<RecentGameResponse>? = null,
    private val ranksResponse: List<RankResponse>? = null,
) : RiotApiClient {
    override fun getSummoner(name: String): SummonerResponse {
        return summonerResponse ?: Assertions.fail("No summonerResponse provided")
    }

    override fun getRecentGames(id: String, limit: Int): List<RecentGameResponse> {
        return recentGamesResponse ?: Assertions.fail("No recentGamesResponse provided")
    }

    override fun getRanks(id: String): List<RankResponse> {
        return ranksResponse ?: Assertions.fail("No ranksResponse provided")
    }
}
