package com.badger.lolbyte.utils

enum class Region(val region: String, val platform: String) {
    BRAZIL("br", "br1"),
    EUROPE_NORTH_EAST("eune", "eun1"),
    EUROPE_WEST("euw", "euw1"),
    JAPAN("jp", "jp1"),
    KOREA("kr", "kr1"),
    LATIN_AMERICA_NORTH("lan", "la1"),
    LATIN_AMERICA_SOUTH("las", "la2"),
    NORTH_AMERICA("na", "na1"),
    OCEANIA("oce", "oc1"),
    RUSSIA("ru", "ru"),
    TURKEY("tr", "tr1");

    companion object {
        private val defaultRegion = NORTH_AMERICA

        fun fromString(string: String?): Region {
            if (string == null) return defaultRegion
            return values().firstOrNull { it.region == string.toLowerCase() } ?: defaultRegion
        }
    }
}
