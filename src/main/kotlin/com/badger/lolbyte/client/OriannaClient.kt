package com.badger.lolbyte.client

import com.badger.lolbyte.recent.ItemResponse
import com.badger.lolbyte.recent.RecentGameResponse
import com.badger.lolbyte.summoner.SummonerResponse
import com.merakianalytics.orianna.Orianna
import com.merakianalytics.orianna.types.common.Region
import com.merakianalytics.orianna.types.core.match.MatchHistory
import com.merakianalytics.orianna.types.core.summoner.Summoner

class OriannaClient(region: com.badger.lolbyte.util.Region, apiKey: String) : RiotApiClient {
    init {
        Orianna.setRiotAPIKey(apiKey)
        val oriannaRegion = when (region) {
            com.badger.lolbyte.util.Region.NORTH_AMERICA -> Region.NORTH_AMERICA
            com.badger.lolbyte.util.Region.BRAZIL -> Region.BRAZIL
            com.badger.lolbyte.util.Region.EUROPE_NORTH_EAST -> Region.EUROPE_NORTH_EAST
            com.badger.lolbyte.util.Region.EUROPE_WEST -> Region.EUROPE_WEST
            com.badger.lolbyte.util.Region.JAPAN -> Region.JAPAN
            com.badger.lolbyte.util.Region.KOREA -> Region.KOREA
            com.badger.lolbyte.util.Region.LATIN_AMERICA_NORTH -> Region.LATIN_AMERICA_NORTH
            com.badger.lolbyte.util.Region.LATIN_AMERICA_SOUTH -> Region.LATIN_AMERICA_SOUTH
            com.badger.lolbyte.util.Region.OCEANIA -> Region.OCEANIA
            com.badger.lolbyte.util.Region.RUSSIA -> Region.RUSSIA
            com.badger.lolbyte.util.Region.TURKEY -> Region.TURKEY
        }
        Orianna.setDefaultRegion(oriannaRegion)
    }

    override fun getSummoner(name: String): SummonerResponse {
        val summoner = Summoner.named(name).get()
        return SummonerResponse(
            summoner.accountId,
            summoner.region.tag.toLowerCase(),
            summoner.name,
            summoner.level,
            summoner.profileIcon.id
        )
    }

    override fun getRecentGames(id: String, limit: Int): List<RecentGameResponse> {
        val summoner = Summoner.withAccountId(id).get()
        val matchHistory = MatchHistory.forSummoner(summoner).withEndIndex(limit).get()
        return matchHistory.map { match ->
            val participant = match.participants.first { it.summoner.accountId == id }
            val items = participant.items.map { item ->
                // TODO description isnt html
                ItemResponse(item.id, item.name, item.description)
            }
            val spells = listOf(participant.summonerSpellD.id, participant.summonerSpellF.id)
            RecentGameResponse(
                id = match.id,
                timestamp = match.creationTime.millis,
                teamId = participant.team.side.id,
                champId = participant.champion.id,
                win = participant.team.isWinner,
                kills = participant.stats.kills,
                deaths = participant.stats.deaths,
                assists = participant.stats.assists,
                wards = participant.stats.wardsPlaced,
                cs = participant.stats.creepScore,
                // TODO this is wrong
                queueName = match.queue.tag,
                duration = match.duration.millis,
                items = items,
                spells = spells,
                // TODO this is wrong
                keystone = participant.primaryRunePath.id
            )
        }
    }
}
