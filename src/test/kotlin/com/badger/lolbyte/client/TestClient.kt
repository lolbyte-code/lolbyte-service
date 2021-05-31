package com.badger.lolbyte.client

import com.badger.lolbyte.recent.RecentGameResponse
import com.badger.lolbyte.summoner.SummonerResponse
import org.junit.jupiter.api.Assertions

class TestClient(
    private val summonerResponse: SummonerResponse? = null,
    private val recentGamesResponse: List<RecentGameResponse>? = null,
) : RiotApiClient {
    override fun getSummoner(name: String): SummonerResponse {
        return summonerResponse ?: Assertions.fail("No summonerResponse provided")
    }

    override fun getRecentGames(id: String, limit: Int): List<RecentGameResponse> {
        return recentGamesResponse ?: Assertions.fail("No recentGamesResponse provided")
    }
}
