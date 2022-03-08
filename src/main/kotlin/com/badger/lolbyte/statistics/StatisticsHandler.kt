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
        val actualLimit = limit ?: kotlin.math.min(recentGames.size, defaultLimit)
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
                // TODO: winPercentage won't be accurate if games < 20
                // But we have to do it this way since the win percentages are hard-coded client side.
                // Also when you divide by numbers != 20, you get a lot of decimals so we have to round here.
                // Might be able to revert this code if R4J / Riot fix invalid match issues causing the count to be
                // messed up here.
                winPercentage = (countWins / defaultLimit.toDouble() * 100).toInt(),
                kills = (countKills / countGames).round(2),
                deaths = (countDeaths / countGames).round(2),
                assists = (countAssists / countGames).round(2),
                wards = (countWards / countGames).round(2),
                games = actualLimit,
            )
        } else {
            PlayerStatsResponse(
                winPercentage = 0,
                kills = 0.0,
                deaths = 0.0,
                assists = 0.0,
                wards = 0.0,
                games = actualLimit,
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

    private fun Double.round(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return kotlin.math.round(this * multiplier) / multiplier
    }
}
