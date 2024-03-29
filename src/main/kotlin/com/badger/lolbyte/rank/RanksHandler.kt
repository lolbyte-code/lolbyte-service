package com.badger.lolbyte.rank

import com.badger.lolbyte.client.LeagueApiClient
import com.badger.lolbyte.utils.LolByteUtils
import kotlin.math.floor

data class RanksResponse(val total: Int, val data: List<RankResponse>)

data class RankResponse(
    val tier: String,
    val rank: String,
    val score: Int,
    val wins: Int,
    val leagueName: String,
    val lp: Int,
    val series: String,
    val queueName: String,
    val queueId: Int,
) {
    constructor(
        tier: String,
        division: String,
        points: Int,
        series: String,
        wins: Int,
        leagueName: String,
        queueName: String,
        queueId: Int
    ) : this(
        tier = if (tier == "emerald") "platinum" else tier,
        rank = LolByteUtils.generateRank(tier, division),
        score = getLolByteScore(tier, division, points),
        wins = wins,
        leagueName = leagueName,
        lp = points,
        series = series,
        queueName = queueName,
        queueId = queueId
    )
}

private fun getLolByteScore(tier: String, division: String, points: Int): Int {
    val divisionScale = when (division) {
        "I" -> 4
        "II" -> 3
        "III" -> 2
        "IV" -> 1
        else -> 0
    }
    val scale = divisionScale * 70 + .7 * points
    return when (tier.toUpperCase()) {
        "IRON" -> floor(450 + scale).toInt()
        "BRONZE" -> floor(800 + scale).toInt()
        "SILVER" -> floor(1150 + scale).toInt()
        "GOLD" -> floor(1500 + scale).toInt()
        "PLATINUM" -> floor(1850 + scale).toInt()
        "EMERALD" -> floor(2200 + scale).toInt()
        "DIAMOND" -> floor(2250 + scale).toInt()
        "MASTER" -> floor(3000 + .7 * points).toInt()
        "GRANDMASTER" -> floor(3350 + .7 * points).toInt()
        "CHALLENGER" -> floor(3700 + .7 * points).toInt()
        else -> 0
    }
}

class RanksHandler(private val client: LeagueApiClient) {
    fun getRanks(id: String): RanksResponse {
        val ranks = client.getRanks(id)
        return RanksResponse(ranks.size, ranks)
    }
}
