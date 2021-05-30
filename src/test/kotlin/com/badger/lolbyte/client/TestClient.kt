package com.badger.lolbyte.client

import com.badger.lolbyte.summoner.SummonerResponse
import com.badger.lolbyte.util.Region
import java.util.UUID

object TestClient : RiotApiClient {
    override fun getSummoner(name: String): SummonerResponse {
        return SummonerResponse(UUID.randomUUID().toString(), Region.NORTH_AMERICA.region, name, 420, 69)
    }
}
