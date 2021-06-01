package com.badger.lolbyte.statistics

import com.badger.lolbyte.client.RiotApiClient

interface StatisticResponse {
    val type: String
}

data class PlayerStatsResponse(
    override val type: String,
    val winPercentage: Int,
    val kills: Double,
    val deaths: Double,
    val assists: Double,
    val wards: Double,
) : StatisticResponse

data class MostPlayedChampsResponse(
    val champs: List<MostPlayedChampResponse>,
) : StatisticResponse {
    override val type: String = "Most Played (Recent)"
}

data class MostPlayedChampResponse(
    val id: Int,
    val name: String,
    val gamesPlayed: Int,
)

data class TopChampsResponse(
    val champs: List<TopChampResponse>,
) : StatisticResponse {
    override val type: String = "Top Champions (Mastery)"
}

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

    fun getStatistics(id: String, limit: Int?, queueId: Int?): List<StatisticResponse> {
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

        val playerStats = PlayerStatsResponse(
            type = "Last ${countGames.toInt()} Matches",
            winPercentage = (countWins / countGames * 100).toInt(),
            kills = countKills / countGames,
            deaths = countDeaths / countGames,
            assists = countAssists / countGames,
            wards = countWards / countGames,
        )

        val mostPlayedChamps = MostPlayedChampsResponse(
            champs.groupBy { it }.values.take(3).map { topChamp ->
                val name = client.getChampName(topChamp.first())
                MostPlayedChampResponse(
                    id = topChamp.first(),
                    name = name,
                    gamesPlayed = topChamp.size
                )
            }
        )

        val topChamps = client.getTopChamps(id, 3)

        return listOf(playerStats, mostPlayedChamps, topChamps)
    }
}
