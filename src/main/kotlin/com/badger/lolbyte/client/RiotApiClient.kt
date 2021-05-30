package com.badger.lolbyte.client

import com.badger.lolbyte.summoner.SummonerResponse

interface RiotApiClient {
    fun getSummoner(name: String): SummonerResponse
}
