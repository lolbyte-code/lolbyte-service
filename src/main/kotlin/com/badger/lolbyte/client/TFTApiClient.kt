package com.badger.lolbyte.client

import com.badger.lolbyte.rank.RankResponse
import com.badger.lolbyte.summoner.SummonerResponse
import com.badger.lolbyte.utils.Region

interface TFTApiClient {
    fun setRegion(region: Region)
    fun getSummoner(name: String): SummonerResponse
    fun getTFTRanks(name: String): List<RankResponse>
}
