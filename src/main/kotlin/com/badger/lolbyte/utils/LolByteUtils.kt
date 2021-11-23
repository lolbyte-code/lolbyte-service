package com.badger.lolbyte.utils

import com.badger.lolbyte.match.PlayerResponse
import kotlin.math.max

object LolByteUtils {
    fun generateRank(tier: String, division: String): String {
        return "${tier[0].toUpperCase()}${tier.substring(1, tier.length).toLowerCase()} $division"
    }

    fun computeKda(player: PlayerResponse?): Double {
        if (player == null) return 0.0
        return player.kills.toDouble() + player.assists.toDouble() / max(player.deaths.toDouble(), 1.0)
    }

    fun divideInts(numerator: Int, denominator: Int): Int {
        return (numerator.toDouble() / denominator.toDouble() * 100).toInt()
    }
}
