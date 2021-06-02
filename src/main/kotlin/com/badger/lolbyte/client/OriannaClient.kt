package com.badger.lolbyte.client

import com.badger.lolbyte.BadRequestException
import com.badger.lolbyte.NotFoundException
import com.badger.lolbyte.current.CurrentGameResponse
import com.badger.lolbyte.match.Badge
import com.badger.lolbyte.match.ItemResponse
import com.badger.lolbyte.match.MatchResponse
import com.badger.lolbyte.match.PlayerResponse
import com.badger.lolbyte.match.TeamResponse
import com.badger.lolbyte.rank.RankResponse
import com.badger.lolbyte.recent.RecentGameResponse
import com.badger.lolbyte.statistics.TopChampResponse
import com.badger.lolbyte.statistics.TopChampsResponse
import com.badger.lolbyte.summoner.SummonerResponse
import com.merakianalytics.orianna.Orianna
import com.merakianalytics.orianna.types.common.Queue
import com.merakianalytics.orianna.types.common.Region
import com.merakianalytics.orianna.types.common.Side
import com.merakianalytics.orianna.types.core.league.LeaguePositions
import com.merakianalytics.orianna.types.core.match.Match
import com.merakianalytics.orianna.types.core.match.MatchHistory
import com.merakianalytics.orianna.types.core.staticdata.Champion
import com.merakianalytics.orianna.types.core.summoner.Summoner
import kotlin.math.max
import com.badger.lolbyte.current.SummonerResponse as CurrentGameSummonerResponse
import com.badger.lolbyte.utils.Queue as LolByteQueue
import com.badger.lolbyte.utils.Region as LolByteRegion

class OriannaClient(apiKey: String, region: LolByteRegion) : RiotApiClient {
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
        if (summoner.accountId == null) throw NotFoundException
        return SummonerResponse(
            summoner.accountId,
            summoner.region.tag.toLowerCase(),
            summoner.name,
            summoner.level,
            summoner.profileIcon.id
        )
    }

    override fun getRecentGames(id: String, limit: Int, queueId: Int?): List<RecentGameResponse> {
        val summoner = Summoner.withAccountId(id).get()
        val matchHistoryBuilder = MatchHistory.forSummoner(summoner).withEndIndex(limit)
        if (queueId != null && queueId != 0) {
            val queue = getQueue(queueId) ?: throw BadRequestException("Invalid queueId $queueId")
            matchHistoryBuilder.withQueues(queue)
        }
        val matchHistory = matchHistoryBuilder.get()
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
                cs = participant.stats.creepScore + participant.stats.neutralMinionsKilled,
                queueName = LolByteQueue.getTag(match.queue.id),
                duration = match.duration.standardMinutes,
                items = if (items.isNotEmpty()) items.subList(0, items.size - 1) else listOf(),
                spells = spells,
                keystone = participant.runeStats.first().rune.id,
            )
        }
    }

    private fun getQueue(queueId: Int): Queue? {
        return Queue.values().firstOrNull { queue ->
            queue.id == queueId
        }
    }

    override fun getRanks(id: String): List<RankResponse> {
        val summoner = Summoner.withAccountId(id).get()
        val leaguePositions = LeaguePositions.forSummoner(summoner).get()
        if (leaguePositions.isEmpty()) {
            return listOf(
                RankResponse(
                    tier = "unranked",
                    division = "",
                    points = 0,
                    wins = 0,
                    series = "",
                    leagueName = "",
                    queueName = "",
                    queueId = 0,
                )
            )
        }
        return leaguePositions.map { entry ->
            val series = if (entry.promos != null) {
                "${entry.promos.wins}W-${entry.promos.losses}L"
            } else {
                ""
            }
            RankResponse(
                tier = entry.tier.name.toLowerCase(),
                division = entry.division.name,
                points = entry.leaguePoints,
                series = series,
                wins = entry.wins,
                leagueName = entry.league.name,
                queueName = LolByteQueue.getTag(entry.queue.id),
                queueId = entry.queue.id,
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
            val entry = try {
                participant.summoner.getLeaguePosition(summoner.currentMatch.queue)
            } catch (e: IllegalArgumentException) {
                participant.summoner.getLeaguePosition(Queue.RANKED_SOLO)
            }
            CurrentGameSummonerResponse(
                participant.summoner.name,
                participant.summoner.accountId == id,
                entry?.tier?.name ?: "unranked",
                entry?.division?.name ?: "",
                participant.champion.id,
                participant.team.side.id,
            )
        }
        return CurrentGameResponse(LolByteQueue.getTag(summoner.currentMatch.queue.id), summoners)
    }

    override fun getMatch(id: Long, summonerId: String): MatchResponse {
        val match = Match.withId(id).get()
        var blueTeamGold = 0
        var blueTeamKills = 0
        var blueTeamDeaths = 0
        var blueTeamAssists = 0
        var blueTeamDamage = 0
        var redTeamGold = 0
        var redTeamKills = 0
        var redTeamDeaths = 0
        var redTeamAssists = 0
        var redTeamDamage = 0

        val players = match.participants.mapIndexed { index, participant ->
            val entry = try {
                participant.summoner.getLeaguePosition(match.queue)
            } catch (e: IllegalArgumentException) {
                participant.summoner.getLeaguePosition(Queue.RANKED_SOLO)
            }
            val items = participant.items.map { item ->
                // TODO not html
                ItemResponse(item.id, item.name, item.description)
            }
            val spells = listOf(participant.summonerSpellD.id, participant.summonerSpellF.id)
            val badges = mutableListOf<Badge>()
            if (participant.team.side == Side.BLUE) {
                blueTeamGold += participant.stats.goldEarned
                blueTeamKills += participant.stats.kills
                blueTeamDeaths += participant.stats.deaths
                blueTeamAssists += participant.stats.assists
                blueTeamDamage += participant.stats.damageDealtToChampions
            } else {
                redTeamGold += participant.stats.goldEarned
                redTeamKills += participant.stats.kills
                redTeamDeaths += participant.stats.deaths
                redTeamAssists += participant.stats.assists
                redTeamDamage += participant.stats.damageDealtToChampions
            }
            if (participant.stats.isFirstBloodKiller) badges.add(Badge.FIRST_BLOOD)
            when {
                participant.stats.pentaKills > 0 -> badges.add(Badge.PENTA_KILL)
                participant.stats.quadraKills > 0 -> badges.add(Badge.QUADRA_KILL)
                participant.stats.tripleKills > 0 -> badges.add(Badge.TRIPLE_KILL)
            }
            PlayerResponse(
                id = participant.summoner.accountId,
                name = participant.summoner.name,
                tier = entry?.tier?.name ?: "unranked",
                division = entry?.division?.name ?: "",
                participantId = index + 1,
                teamId = participant.team.side.id,
                champId = participant.champion.id,
                champName = participant.champion.name,
                kills = participant.stats.kills,
                deaths = participant.stats.deaths,
                assists = participant.stats.assists,
                gold = participant.stats.goldEarned,
                damage = participant.stats.damageDealtToChampions,
                cs = participant.stats.creepScore + participant.stats.neutralMinionsKilled,
                level = participant.stats.championLevel,
                win = participant.team.isWinner,
                wards = participant.stats.wardsPlaced,
                lane = participant.lane.name,
                role = participant.role.name,
                items = if (items.isNotEmpty()) items.subList(0, items.size - 1) else listOf(),
                trinket = items.last().id,
                spells = spells,
                keystone = participant.runeStats.first().rune.id,
                badges = badges,
            )
        }

        val blueTeam = TeamResponse(
            id = match.blueTeam.side.id,
            win = match.blueTeam.isWinner,
            towers = match.blueTeam.towerKills,
            dragons = match.blueTeam.dragonKills,
            barons = match.blueTeam.baronKills,
            gold = blueTeamGold,
            kills = blueTeamKills,
            deaths = blueTeamDeaths,
            assists = blueTeamAssists,
            bans = match.blueTeam.bans.map { it.id }
        )

        val redTeam = TeamResponse(
            id = match.redTeam.side.id,
            win = match.redTeam.isWinner,
            towers = match.redTeam.towerKills,
            dragons = match.redTeam.dragonKills,
            barons = match.redTeam.baronKills,
            gold = redTeamGold,
            kills = redTeamKills,
            deaths = redTeamDeaths,
            assists = redTeamAssists,
            bans = match.redTeam.bans.map { it.id }
        )

        var maxKda: PlayerResponse? = null
        var maxWards: PlayerResponse? = null
        var maxDamage: PlayerResponse? = null
        var maxGold: PlayerResponse? = null
        players.forEach { player ->
            if (maxKda == null || computeKda(player) > computeKda(maxKda)) maxKda = player
            if (player.wards > maxWards?.wards ?: 0) maxWards = player
            if (player.damage > maxDamage?.damage ?: 0) maxDamage = player
            if (player.gold > maxGold?.gold ?: 0) maxGold = player
            if (player.teamId == Side.BLUE.id) {
                player.damageContribution = divideInts(player.damage, blueTeamDamage)
                player.killParticipation = divideInts(player.kills + player.assists, blueTeamKills)
            } else {
                player.damageContribution = divideInts(player.damage, redTeamDamage)
                player.killParticipation = divideInts(player.kills + player.assists, redTeamKills)
            }
        }

        maxKda?.badges?.add(Badge.HIGHEST_KDA)
        maxWards?.badges?.add(Badge.MOST_WARDS)
        maxDamage?.badges?.add(Badge.MOST_DAMAGE)
        maxGold?.badges?.add(Badge.MOST_GOLD)

        return MatchResponse(
            id = match.id,
            blueTeam = blueTeam,
            redTeam = redTeam,
            players = players.sortedBy { it.order },
        )
    }

    private fun computeKda(player: PlayerResponse?): Double {
        if (player == null) return 0.0
        return player.kills.toDouble() + player.assists.toDouble() / max(player.deaths.toDouble(), 1.0)
    }

    private fun divideInts(numerator: Int, denominator: Int): Int {
        return (numerator.toDouble() / denominator.toDouble() * 100).toInt()
    }
}
