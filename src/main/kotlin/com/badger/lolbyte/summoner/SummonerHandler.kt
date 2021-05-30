package com.badger.lolbyte.summoner

import com.badger.lolbyte.client.RiotApiClient

data class SummonerResponse(
    val id: String,
    val region: String,
    val name: String,
    val level: Int,
    val icon: Int
)

class SummonerHandler(private val client: RiotApiClient) {
    fun getSummoner(name: String): SummonerResponse {
        return client.getSummoner(name)
    }
}
