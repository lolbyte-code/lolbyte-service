package com.badger.lolbyte.client

import com.badger.lolbyte.summoner.SummonerResponse
import com.merakianalytics.orianna.Orianna
import com.merakianalytics.orianna.types.common.Region
import com.merakianalytics.orianna.types.core.summoner.Summoner

class OriannaClient(region: com.badger.lolbyte.util.Region, apiKey: String) : RiotApiClient {
    init {
        Orianna.setRiotAPIKey(apiKey)
        val oriannaRegion = when (region) {
            com.badger.lolbyte.util.Region.NORTH_AMERICA -> Region.NORTH_AMERICA
            com.badger.lolbyte.util.Region.BRAZIL -> Region.BRAZIL
            com.badger.lolbyte.util.Region.EUROPE_NORTH_EAST -> Region.EUROPE_NORTH_EAST
            com.badger.lolbyte.util.Region.EUROPE_WEST -> Region.EUROPE_WEST
            com.badger.lolbyte.util.Region.JAPAN -> Region.JAPAN
            com.badger.lolbyte.util.Region.KOREA -> Region.KOREA
            com.badger.lolbyte.util.Region.LATIN_AMERICA_NORTH -> Region.LATIN_AMERICA_NORTH
            com.badger.lolbyte.util.Region.LATIN_AMERICA_SOUTH -> Region.LATIN_AMERICA_SOUTH
            com.badger.lolbyte.util.Region.OCEANIA -> Region.OCEANIA
            com.badger.lolbyte.util.Region.RUSSIA -> Region.RUSSIA
            com.badger.lolbyte.util.Region.TURKEY -> Region.TURKEY
        }
        Orianna.setDefaultRegion(oriannaRegion)
    }

    override fun getSummoner(name: String): SummonerResponse {
        val summoner = Summoner.named(name).get()
        return SummonerResponse(
            summoner.id,
            summoner.region.tag.toLowerCase(),
            summoner.name,
            summoner.level,
            summoner.profileIcon.id
        )
    }
}
