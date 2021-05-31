package com.badger.lolbyte.recent

import com.badger.lolbyte.client.RiotApiClient
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
    val keystone: Int
)

class RecentGamesHandler(private val client: RiotApiClient) {
    companion object {
        private const val defaultLimit = 20
    }

    fun getRecentGames(id: String, limit: Int?, queueId: Int?): RecentGamesResponse {
        val games = client.getRecentGames(id, limit ?: defaultLimit, queueId)
        return RecentGamesResponse(games.size, games)
    }
}
