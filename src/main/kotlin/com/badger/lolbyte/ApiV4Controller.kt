package com.badger.lolbyte

import com.badger.lolbyte.client.HybridClient
import com.badger.lolbyte.client.RetryClient
import com.badger.lolbyte.config.NotificationProperties
import com.badger.lolbyte.config.RetryProperties
import com.badger.lolbyte.config.RiotProperties
import com.badger.lolbyte.current.CurrentGameHandler
import com.badger.lolbyte.current.CurrentGameResponse
import com.badger.lolbyte.match.MatchHandler
import com.badger.lolbyte.match.MatchResponse
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
import com.badger.lolbyte.utils.Region
import com.badger.lolbyte.utils.Retrier
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
class BadRequestException(msg: String) : RuntimeException(msg)

@ResponseStatus(code = HttpStatus.NOT_FOUND)
object NotFoundException : RuntimeException()

@RestController
@RequestMapping("api/v4")
class ApiV4Controller(
    riotProperties: RiotProperties,
    private val notificationProperties: NotificationProperties,
    retryProperties: RetryProperties,
) {
    private val hybridClient = HybridClient(riotProperties.leagueApiKey, riotProperties.tftApiKey)
    private val retrier = Retrier(retryProperties.intervalInSeconds, retryProperties.attempts)
    private val client = RetryClient(hybridClient, retrier)
    // Dev mode
    // private val client = HybridClient(riotProperties.devApiKey, riotProperties.devApiKey)

    @GetMapping("/notifications")
    fun getNotification(
        @RequestParam requestParams: Map<String, String>?
    ): NotificationResponse {
        // Suppress alerts for mobile
        val alert = if (requestParams != null && requestParams.containsKey("mobile")) "" else notificationProperties.alert
        return NotificationHandler.getNotification(alert)
    }

    @GetMapping("/summoners/{name}")
    fun getSummoner(
        @PathVariable name: String,
        @RequestParam(name = "region", required = false) region: String?,
    ): SummonerResponse {
        client.setRegion(Region.fromString(region))
        return SummonerHandler(client).getSummoner(name)
    }

    @GetMapping("/recentGames/{id}")
    fun getRecentGames(
        @PathVariable id: String,
        @RequestParam(name = "limit", required = false) limit: Int?,
        @RequestParam(name = "queueId", required = false) queueId: Int?,
        @RequestParam(name = "region", required = false) region: String?,
    ): RecentGamesResponse {
        client.setRegion(Region.fromString(region))
        return RecentGamesHandler(client).getRecentGames(id, limit, queueId)
    }

    @GetMapping("/statistics/{id}")
    fun getStatistics(
        @PathVariable id: String,
        @RequestParam(name = "limit", required = false) limit: Int?,
        @RequestParam(name = "queueId", required = false) queueId: Int?,
        @RequestParam(name = "region", required = false) region: String?,
    ): StatisticsResponse {
        client.setRegion(Region.fromString(region))
        return StatisticsHandler(client).getStatistics(id, limit, queueId)
    }

    @GetMapping("/ranks/{id}")
    fun getRanks(
        @PathVariable id: String,
        @RequestParam(name = "region", required = false) region: String?,
    ): RanksResponse {
        client.setRegion(Region.fromString(region))
        return RanksHandler(client).getRanks(id)
    }

    @GetMapping("/current/{id}")
    fun getCurrentGame(
        @PathVariable id: String,
        @RequestParam(name = "region", required = false) region: String?,
    ): CurrentGameResponse {
        client.setRegion(Region.fromString(region))
        return CurrentGameHandler(client).getCurrentGame(id)
    }

    @GetMapping("/matches/{id}")
    fun getMatch(
        @PathVariable id: Long,
        @RequestParam(name = "summonerId") summonerId: String,
        @RequestParam(name = "region", required = false) region: String?,
    ): MatchResponse {
        client.setRegion(Region.fromString(region))
        return MatchHandler(client).getMatch(id, summonerId)
    }
}
