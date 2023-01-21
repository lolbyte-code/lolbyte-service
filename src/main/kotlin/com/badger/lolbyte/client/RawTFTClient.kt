package com.badger.lolbyte.client

import com.badger.lolbyte.rank.RankResponse
import com.badger.lolbyte.summoner.SummonerResponse
import com.badger.lolbyte.utils.Queue
import com.badger.lolbyte.utils.Region
import com.google.gson.Gson
import no.stelar7.api.r4j.pojo.lol.summoner.Summoner
import no.stelar7.api.r4j.pojo.tft.league.TFTLeagueEntry
import java.lang.Exception
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class RawTFTClient(apiKey: String) : TFTApiClient {
    private val tftSummonerUrl: String = "https://%s.api.riotgames.com/tft/summoner/v1/summoners/by-name/%s?api_key=$apiKey"
    private val tftRanksUrl: String = "https://%s.api.riotgames.com/tft/league/v1/entries/by-summoner/%s?api_key=$apiKey"

    private var region = Region.NORTH_AMERICA

    override fun setRegion(region: Region) {
        this.region = region
    }

    override fun getSummoner(name: String): SummonerResponse {
        val urlEncodedName = URLEncoder.encode(name, "utf-8")
        val url = String.format(tftSummonerUrl, this.region.platform, urlEncodedName)
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        val summoner = Gson().fromJson(response.body(), Summoner::class.java)
        return SummonerResponse(
            summoner.summonerId,
            this.region.region,
            summoner.name,
            summoner.summonerLevel,
            summoner.profileIconId,
        )
    }

    override fun getTFTRanks(name: String): List<RankResponse> {
        // Yes this is hacky. No I don't care. This whole client should be
        // removed once R4J handles credentials state non-globally.
        return try {
            getTFTRanksHelper(name)
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun getTFTRanksHelper(name: String): List<RankResponse> {
        val summonerId = getSummoner(name).id
        val url = String.format(tftRanksUrl, this.region.platform, summonerId)
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        val tftLeagueEntries = Gson().fromJson(response.body(), Array<TFTLeagueEntry>::class.java)
        // Filter out other TFT game types
        return tftLeagueEntries.filter { it.ratedTier == null }.map { entry ->
            RankResponse(
                tier = entry.tier.toLowerCase(),
                division = entry.rank,
                points = entry.leaguePoints,
                series = "",
                wins = entry.wins,
                leagueName = "",
                queueName = Queue.RANKED_TFT.tag,
                queueId = Queue.RANKED_TFT.id,
            )
        }
    }
}
