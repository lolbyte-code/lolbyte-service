package com.badger.lolbyte.statistics

import com.badger.lolbyte.client.RiotApiClient

data class StatisticsResponse(val playerStats: List<PlayerStatsResponse>)

data class PlayerStatsResponse(
    val type: String,
    val winPercentage: Int,
    val kills: Double,
    val deaths: Double,
    val assists: Double,
    val wards: Double,
)

class StatisticsHandler(private val client: RiotApiClient) {
    companion object {
        private const val defaultLimit = 20
    }

    fun getStatistics(id: String, limit: Int?): StatisticsResponse {
        val recentGames = client.getRecentGames(id, limit ?: defaultLimit)
        var countWins = 0
        var countKills = 0
        var countDeaths = 0
        var countAssists = 0
        var countWards = 0
        var countGames = 0.0
        recentGames.forEach { game ->
            if (game.win) countWins++
            countKills += game.kills
            countDeaths += game.deaths
            countAssists += game.assists
            countWards += game.wards
            countGames++
        }
        return StatisticsResponse(
            listOf(
                PlayerStatsResponse(
                    "Last ${countGames.toInt()} Matches",
                    (countWins / countGames * 100).toInt(),
                    countKills / countGames,
                    countDeaths / countGames,
                    countAssists / countGames,
                    countWards / countGames,
                )
            )
        )
    }
}
