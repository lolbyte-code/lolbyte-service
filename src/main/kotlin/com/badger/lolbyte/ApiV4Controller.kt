package com.badger.lolbyte

import com.badger.lolbyte.client.OriannaClient
import com.badger.lolbyte.config.RiotProperties
import com.badger.lolbyte.notification.NotificationHandler
import com.badger.lolbyte.notification.NotificationResponse
import com.badger.lolbyte.rank.RanksHandler
import com.badger.lolbyte.rank.RanksResponse
import com.badger.lolbyte.recent.RecentGamesHandler
import com.badger.lolbyte.recent.RecentGamesResponse
import com.badger.lolbyte.statistics.StatisticsHandler
import com.badger.lolbyte.statistics.StatisticsResponse
import com.badger.lolbyte.summoner.SummonerHandler
import com.badger.lolbyte.summoner.SummonerResponse
import com.badger.lolbyte.util.Region
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v4")
class ApiV4Controller(
    @RequestParam(name = "region") region: String?,
    riotProperties: RiotProperties
) {
    private val client = OriannaClient(Region.fromString(region), riotProperties.apiKey)

    @GetMapping("/notifications")
    fun getNotification(): NotificationResponse {
        return NotificationHandler.getNotification()
    }

    @GetMapping("/summoners/{name}")
    fun getSummoner(
        @PathVariable name: String
    ): SummonerResponse {
        return SummonerHandler(client).getSummoner(name)
    }

    // TODO gametype query param
    @GetMapping("/recentGames/{id}")
    fun getRecentGames(
        @PathVariable id: String,
        @RequestParam(name = "limit", required = false) limit: Int?
    ): RecentGamesResponse {
        return RecentGamesHandler(client).getRecentGames(id, limit)
    }

    // TODO gametype query param
    @GetMapping("/statistics/{id}")
    fun getStatistics(
        @PathVariable id: String,
        @RequestParam(name = "limit", required = false) limit: Int?
    ): StatisticsResponse {
        return StatisticsHandler(client).getStatistics(id, limit)
    }

    @GetMapping("/ranks/{id}")
    fun getRanks(
        @PathVariable id: String,
    ): RanksResponse {
        return RanksHandler(client).getRanks(id)
    }
}
