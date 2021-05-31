package com.badger.lolbyte.current

import com.badger.lolbyte.client.RiotApiClient

data class CurrentGameResponse(
    val queueName: String,
    val summoners: List<SummonerResponse>
)

data class SummonerResponse(
    val name: String,
    val selected: Boolean,
    val rank: String,
    val champId: Int,
    val teamId: Int,
) {
    constructor(
        name: String,
        selected: Boolean,
        tier: String,
        division: String,
        champId: Int,
        teamId: Int
    ) : this(name, selected, "$tier $division", champId, teamId)
}

class CurrentGameHandler(private val client: RiotApiClient) {
    fun getCurrentGame(id: String): CurrentGameResponse {
        return client.getCurrentGame(id)
    }
}
