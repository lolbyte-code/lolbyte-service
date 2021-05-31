package com.badger.lolbyte.client

import com.badger.lolbyte.NotFoundException
import com.badger.lolbyte.current.CurrentGameResponse
import com.badger.lolbyte.rank.RankResponse
import com.badger.lolbyte.recent.ItemResponse
import com.badger.lolbyte.recent.RecentGameResponse
import com.badger.lolbyte.statistics.TopChampResponse
import com.badger.lolbyte.statistics.TopChampsResponse
import com.badger.lolbyte.summoner.SummonerResponse
import com.merakianalytics.orianna.Orianna
import com.merakianalytics.orianna.types.common.Region
import com.merakianalytics.orianna.types.core.league.LeaguePositions
import com.merakianalytics.orianna.types.core.match.MatchHistory
import com.merakianalytics.orianna.types.core.staticdata.Champion
import com.merakianalytics.orianna.types.core.summoner.Summoner
import com.badger.lolbyte.current.SummonerResponse as CurrentGameSummonerResponse
import com.badger.lolbyte.region.Region as LolByteRegion

// TODO: make sure impls here are efficient
class OriannaClient(region: LolByteRegion, apiKey: String) : RiotApiClient {
    init {
        Orianna.setRiotAPIKey(apiKey)
        val oriannaRegion = when (region) {
            LolByteRegion.NORTH_AMERICA -> Region.NORTH_AMERICA
            LolByteRegion.BRAZIL -> Region.BRAZIL
            LolByteRegion.EUROPE_NORTH_EAST -> Region.EUROPE_NORTH_EAST
            LolByteRegion.EUROPE_WEST -> Region.EUROPE_WEST
            LolByteRegion.JAPAN -> Region.JAPAN
            LolByteRegion.KOREA -> Region.KOREA
            LolByteRegion.LATIN_AMERICA_NORTH -> Region.LATIN_AMERICA_NORTH
            LolByteRegion.LATIN_AMERICA_SOUTH -> Region.LATIN_AMERICA_SOUTH
            LolByteRegion.OCEANIA -> Region.OCEANIA
            LolByteRegion.RUSSIA -> Region.RUSSIA
            LolByteRegion.TURKEY -> Region.TURKEY
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

    override fun getRanks(id: String): List<RankResponse> {
        val summoner = Summoner.withAccountId(id).get()
        val leaguePositions = LeaguePositions.forSummoner(summoner).get()
        return leaguePositions.map { entry ->
            RankResponse(
                entry.tier.name,
                entry.division.name,
                entry.leaguePoints,
                entry.wins,
                entry.league.name,
                entry.queue.tag,
                entry.queue.id,
            )
        }
    }

    override fun getTopChamps(id: String, limit: Int): TopChampsResponse {
        val summoner = Summoner.withAccountId(id).get()
        val topChamps = summoner.championMasteries.take(limit).map { championMastery ->
            TopChampResponse(
                championMastery.champion.id,
                championMastery.champion.name,
                championMastery.level,
                championMastery.points,
            )
        }
        return TopChampsResponse(topChamps)
    }

    override fun getChampName(id: Int): String {
        return Champion.withId(id).get().name
    }

    override fun getCurrentGame(id: String): CurrentGameResponse {
        val summoner = Summoner.withAccountId(id).get()
        if (!summoner.isInGame) throw NotFoundException
        val summoners = summoner.currentMatch.participants.map { participant ->
            val entry = summoner.getLeaguePosition(summoner.currentMatch.queue)
            CurrentGameSummonerResponse(
                participant.summoner.name,
                participant.summoner.accountId == id,
                entry.tier.name,
                entry.division.name,
                participant.champion.id,
                participant.team.side.id,
            )
        }
        return CurrentGameResponse(summoner.currentMatch.mode.name, summoners)
    }
}
