package com.badger.lolbyte.utils

object LolByteUtils {
    fun generateRank(tier: String, division: String): String {
        return "${tier[0].toUpperCase()}${tier.substring(1, tier.length).toLowerCase()} $division"
    }
}
