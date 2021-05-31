package com.badger.lolbyte.utils

enum class Region(val region: String) {
    BRAZIL("br"),
    EUROPE_NORTH_EAST("eune"),
    EUROPE_WEST("euw"),
    JAPAN("jp"),
    KOREA("kr"),
    LATIN_AMERICA_NORTH("lan"),
    LATIN_AMERICA_SOUTH("las"),
    NORTH_AMERICA("na"),
    OCEANIA("oce"),
    RUSSIA("ru"),
    TURKEY("tr");

    companion object {
        private val defaultRegion = NORTH_AMERICA

        fun fromString(string: String?): Region {
            if (string == null) return defaultRegion
            return values().firstOrNull { it.region == string.toLowerCase() } ?: defaultRegion
        }
    }
}
