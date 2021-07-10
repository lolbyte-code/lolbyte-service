package com.badger.lolbyte.utils

object LolByteUtils {
    private val validSpells = setOf(0, 1, 3, 4, 6, 7, 11, 12, 13, 14, 21, 30, 31, 32, 33, 34, 35, 36, 39, 50, 51, 52)

    fun generateRank(tier: String, division: String): String {
        return "${tier[0].toUpperCase()}${tier.substring(1, tier.length).toLowerCase()} $division"
    }

    fun sanitizeSpell(id: Int): Int {
        return if (id in validSpells) id else 0
    }
}
