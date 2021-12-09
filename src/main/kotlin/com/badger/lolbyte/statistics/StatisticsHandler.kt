package com.badger.lolbyte.statistics

import com.badger.lolbyte.client.RiotApiClient

data class StatisticsResponse(
    val playerStats: PlayerStatsResponse,
    val mostPlayedChamps: MostPlayedChampsResponse,
    val topChamps: TopChampsResponse,
)

data class PlayerStatsResponse(
    val winPercentage: Int,
    val kills: Double,
    val deaths: Double,
    val assists: Double,
    val wards: Double,
    val games: Int,
)

data class MostPlayedChampsResponse(
    val champs: List<MostPlayedChampResponse>,
)

data class MostPlayedChampResponse(
    val id: Int,
    val name: String,
    val gamesPlayed: Int,
)

data class TopChampsResponse(
    val champs: List<TopChampResponse>,
)

data class TopChampResponse(
    val id: Int,
    val name: String,
    val level: Int,
    val points: Int,
)

class StatisticsHandler(private val client: RiotApiClient) {
    companion object {
        private const val defaultLimit = 20
    }

    fun getStatistics(id: String, limit: Int?, queueId: Int?): StatisticsResponse {
        val recentGames = client.getRecentGames(id, limit ?: defaultLimit, queueId)
        var countWins = 0
        var countKills = 0
        var countDeaths = 0
        var countAssists = 0
        var countWards = 0
        val champs = mutableListOf<Int>()
        var countGames = 0.0
        recentGames.forEach { game ->
            if (game.win) countWins++
            countKills += game.kills
            countDeaths += game.deaths
            countAssists += game.assists
            countWards += game.wards
            champs.add(game.champId)
            countGames++
        }

        val playerStats = if (countGames > 0) {
            PlayerStatsResponse(
                winPercentage = (countWins / countGames * 100).toInt(),
                kills = countKills / countGames,
                deaths = countDeaths / countGames,
                assists = countAssists / countGames,
                wards = countWards / countGames,
                games = limit ?: defaultLimit,
            )
        } else {
            PlayerStatsResponse(
                winPercentage = 0,
                kills = 0.0,
                deaths = 0.0,
                assists = 0.0,
                wards = 0.0,
                games = limit ?: defaultLimit,
            )
        }

        // Some weird issue where champ ids can be invalid in recent games.
        champs.removeIf {
            try {
                client.getChampName(it)
                false
            } catch (e: Exception) {
                true
            }
        }

        val mostPlayedChamps = MostPlayedChampsResponse(
            champs.groupBy { it }.values.sortedByDescending { it.size }.take(3).map { topChamp ->
                val name = client.getChampName(topChamp.first())
                MostPlayedChampResponse(
                    id = topChamp.first(),
                    name = name,
                    gamesPlayed = topChamp.size
                )
            }
        )

        val topChamps = client.getTopChamps(id, 3)

        return StatisticsResponse(
            playerStats = playerStats,
            mostPlayedChamps = mostPlayedChamps,
            topChamps = topChamps
        )
    }
}
