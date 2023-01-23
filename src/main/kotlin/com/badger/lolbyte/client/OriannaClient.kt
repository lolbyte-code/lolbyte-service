package com.badger.lolbyte.client

import com.badger.lolbyte.current.CurrentGameResponse
import com.badger.lolbyte.match.MatchResponse
import com.badger.lolbyte.rank.RankResponse
import com.badger.lolbyte.recent.RecentGameResponse
import com.badger.lolbyte.statistics.TopChampsResponse
import com.badger.lolbyte.summoner.SummonerResponse
import com.merakianalytics.orianna.Orianna
import com.merakianalytics.orianna.types.common.Region
import com.merakianalytics.orianna.types.core.staticdata.Champion
import com.badger.lolbyte.utils.Region as LolByteRegion

class OriannaClient(apiKey: String) : LeagueApiClient {
    init {
        Orianna.setRiotAPIKey(apiKey)
    }

    override fun setRegion(region: LolByteRegion) {
        val oriannaRegion = when (region) {
            LolByteRegion.NORTH_AMERICA -> Region.NORTH_AMERICA
            LolByteRegion.BRAZIL -> Region.BRAZIL
            LolByteRegion.EUROPE_NORTH_EAST -> Region.EUROPE_NORTH_EAST
            LolByteRegion.EUROPE_WEST -> Region.EUROPE_WEST
            LolByteRegion.JAPAN -> Region.JAPAN
            LolByteRegion.KOREA -> Region.KOREA
            LolByteRegion.LATIN_AMERICA_NORTH -> Region.LATIN_AMERICA_NORTH
            LolByteRegion.LATIN_AMERICA_SOUTH -> Region.LATIN_AMERICA_SOUTH
            LolByteRegion.OCEANIA -> Region.OCEANIA
            LolByteRegion.RUSSIA -> Region.RUSSIA
            LolByteRegion.TURKEY -> Region.TURKEY
        }
        Orianna.setDefaultRegion(oriannaRegion)
    }

    override fun getSummoner(name: String): SummonerResponse {
        throw UnsupportedOperationException("getSummoner not implemented!")
    }

    override fun getSummonerById(id: String): SummonerResponse {
        throw UnsupportedOperationException("getSummonerById not implemented")
    }

    override fun getRecentGames(id: String, limit: Int, queueId: Int?): List<RecentGameResponse> {
        throw UnsupportedOperationException("getRecentGames not implemented")
    }

    override fun getRanks(id: String): List<RankResponse> {
        throw UnsupportedOperationException("getRanks not implemented")
    }

    override fun getTopChamps(id: String, limit: Int): TopChampsResponse {
        throw UnsupportedOperationException("getTopChamps not implemented")
    }

    override fun getChampName(id: Int): String {
        return Champion.withId(id).get().name
    }

    override fun getCurrentGame(id: String): CurrentGameResponse {
        throw UnsupportedOperationException("getCurrentGame not implemented")
    }

    override fun getMatch(id: Long, summonerId: String): MatchResponse {
        throw UnsupportedOperationException("getMatch not implemented")
    }
}
