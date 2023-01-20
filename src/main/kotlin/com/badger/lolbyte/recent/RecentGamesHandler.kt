package com.badger.lolbyte.recent

import com.badger.lolbyte.client.LeagueApiClient
import com.badger.lolbyte.match.ItemResponse

data class RecentGamesResponse(
    val total: Int,
    val data: List<RecentGameResponse>
)

data class RecentGameResponse(
    val id: Long,
    val timestamp: Long,
    val teamId: Int,
    val champId: Int,
    val win: Boolean,
    val kills: Int,
    val deaths: Int,
    val assists: Int,
    val wards: Int,
    val cs: Int,
    val queueName: String,
    val duration: Long,
    val items: List<ItemResponse>,
    val spells: List<Int>,
    val keystone: Int,
    val gameMode: String = "",
)

class RecentGamesHandler(private val client: LeagueApiClient) {
    companion object {
        private const val defaultLimit = 10
    }

    fun getRecentGames(id: String, limit: Int?, queueId: Int?): RecentGamesResponse {
        val games = client.getRecentGames(id, limit ?: defaultLimit, queueId)
        return RecentGamesResponse(games.size, games)
    }
}
