package com.badger.lolbyte

import com.badger.lolbyte.client.OriannaClient
import com.badger.lolbyte.config.RiotProperties
import com.badger.lolbyte.summoner.SummonerHandler
import com.badger.lolbyte.summoner.SummonerResponse
import com.badger.lolbyte.util.Region
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v4")
class ApiV4Controller(@RequestParam(name = "region") region: String?, riotProperties: RiotProperties) {
    private val client = OriannaClient(Region.fromString(region), riotProperties.apiKey)

    @GetMapping("/summoners")
    fun getSummoner(@RequestParam(name = "name") name: String): SummonerResponse {
        return SummonerHandler(client).getSummoner(name)
    }
}
