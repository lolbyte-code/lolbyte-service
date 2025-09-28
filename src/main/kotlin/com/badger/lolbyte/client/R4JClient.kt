package com.badger.lolbyte.client

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
import com.badger.lolbyte.utils.Cache
import com.badger.lolbyte.utils.LolByteUtils
import com.badger.lolbyte.utils.Queue
import com.badger.lolbyte.utils.Region
import no.stelar7.api.r4j.basic.APICredentials
import no.stelar7.api.r4j.basic.cache.impl.EmptyCacheProvider
import no.stelar7.api.r4j.basic.calling.DataCall
import no.stelar7.api.r4j.basic.constants.api.regions.LeagueShard
import no.stelar7.api.r4j.basic.constants.types.ApiKeyType
import no.stelar7.api.r4j.basic.constants.types.lol.GameQueueType
import no.stelar7.api.r4j.basic.constants.types.lol.TeamType
import no.stelar7.api.r4j.impl.R4J
import no.stelar7.api.r4j.impl.R4J.LOLAPI
import no.stelar7.api.r4j.impl.R4J.TFTAPI
import no.stelar7.api.r4j.impl.lol.builders.matchv5.match.MatchBuilder
import no.stelar7.api.r4j.impl.lol.raw.DDragonAPI
import no.stelar7.api.r4j.impl.shared.AccountAPI
import no.stelar7.api.r4j.pojo.lol.match.v5.MatchParticipant
import no.stelar7.api.r4j.pojo.lol.summoner.Summoner
import java.util.stream.Collectors
import com.badger.lolbyte.current.SummonerResponse as CurrentGameSummonerResponse

class R4JClient(leagueApiKey: String, tftApiKey: String) : LeagueApiClient {
    private val leagueAPI: LOLAPI
    private val tftAPI: TFTAPI
    private val accountAPI: AccountAPI
    private val dDragonAPI: DDragonAPI
    private var leagueShard = LeagueShard.NA1

    private val items = Cache.buildCache<Int, ItemResponse>(Cache.ETERNAL_TTL)
    private val champs = Cache.buildCache<Int, String>(Cache.ETERNAL_TTL)

    init {
        val credentials = APICredentials(leagueApiKey, "", tftApiKey, "", leagueApiKey)
        val r4J = R4J(credentials)
        DataCall.setCacheProvider(EmptyCacheProvider.INSTANCE)
        leagueAPI = r4J.loLAPI
        tftAPI = r4J.tftapi
        accountAPI = r4J.accountAPI
        dDragonAPI = r4J.dDragonAPI

        dDragonAPI.items.forEach {
            items.put(it.key, ItemResponse(it.value.id, it.value.name, it.value.description))
        }

        dDragonAPI.champions.forEach {
            champs.put(it.key, it.value.name)
        }
    }

    override fun setRegion(region: Region) {
        val leagueShard = when (region) {
            Region.NORTH_AMERICA -> LeagueShard.NA1
            Region.BRAZIL -> LeagueShard.BR1
            Region.EUROPE_NORTH_EAST -> LeagueShard.EUN1
            Region.EUROPE_WEST -> LeagueShard.EUW1
            Region.JAPAN -> LeagueShard.JP1
            Region.KOREA -> LeagueShard.KR
            Region.LATIN_AMERICA_NORTH -> LeagueShard.LA1
            Region.LATIN_AMERICA_SOUTH -> LeagueShard.LA2
            Region.OCEANIA -> LeagueShard.OC1
            Region.RUSSIA -> LeagueShard.RU
            Region.TURKEY -> LeagueShard.TR1
        }
        this.leagueShard = leagueShard
    }

    override fun getSummoner(name: String): SummonerResponse {
        val nameWithTag = if (name.contains("#")) {
            name
        } else {
            "$name#${leagueShard.value}"
        }
        val (gameName, tag) = nameWithTag.split("#")
        val account = accountAPI.getAccountByTag(leagueShard.toRegionShard(), gameName, tag)
        val summoner = leagueAPI.summonerAPI.getSummonerByPUUID(leagueShard, account.puuid)
        return SummonerResponse(
            id = summoner.puuid,
            region = leagueShard.realmValue,
            name = "${account.name}#${account.tag}",
            level = summoner.summonerLevel,
            icon = summoner.profileIconId,
        )
    }

    override fun getRecentGames(id: String, limit: Int, queueId: Int?): List<RecentGameResponse> {
        val regionShard = leagueShard.toRegionShard()

        val gameQueueTypes = when (queueId) {
            420 -> rankedGameQueueTypes
            700 -> clashGameQueueTypes
            else -> defaultGameQueueTypes
        }

        val fullMatchList = gameQueueTypes.flatMap {
            leagueAPI.matchAPI.getMatchList(regionShard, id, it, null, null, limit, null, null)
        }

        return getFirstNMatches(fullMatchList, limit).parallelStream().map { matchId ->
            leagueAPI.matchAPI.getMatch(regionShard, matchId)
        }.filter {
            it.gameCreation != 0L
        }.map { match ->
            val participant = match.participants.first { it.puuid == id }
            val items = getItems(participant)
            val spells = listOf(participant.summoner1Id, participant.summoner2Id)
            RecentGameResponse(
                id = match.gameId,
                timestamp = match.gameStartTimestamp,
                teamId = participant.team.value,
                champId = participant.championId,
                win = participant.didWin(),
                kills = participant.kills,
                deaths = participant.deaths,
                assists = participant.assists,
                wards = participant.wardsPlaced,
                cs = participant.totalMinionsKilled + participant.neutralMinionsKilled,
                queueName = Queue.getTag(match.queue.values.firstOrNull()),
                duration = match.gameDurationAsDuration.toMinutes(),
                items = if (items.isNotEmpty()) items.subList(0, items.size - 1) else listOf(),
                spells = spells,
                // Perks are intermittently null.
                keystone = participant.perks.perkStyles.firstOrNull()?.selections?.firstOrNull()?.perk ?: 0,
                gameMode = match.gameMode.value,
            )
        }.collect(Collectors.toList())
    }

    // Match IDs are monotonically increasing, so we can get the last X matches by sorting on the ID.
    private fun getFirstNMatches(matchList: List<String?>, limit: Int): List<String> {
        // Matches with valid match ids can be null (Riot API bug).
        return matchList.filterNotNull().sortedByDescending { it.split('_').last().toLong() }.take(limit)
    }

    private fun getItems(participant: MatchParticipant): List<ItemResponse> {
        val participantItems = listOf(
            participant.item0,
            participant.item1,
            participant.item2,
            participant.item3,
            participant.item4,
            participant.item5,
            participant.item6
        )
        return participantItems.mapNotNull { itemId ->
            items[itemId]
        }
    }

    override fun getRanks(id: String): List<RankResponse> {
        val leagueRanks = getLeagueRanks(id)
        // Ids are specific to API key
        val account = accountAPI.getAccountByPUUID(leagueShard.toRegionShard(), id)
        val tftRanks = getTFTRanks(account.name, account.tag)
        return if (leagueRanks.first().tier == "unranked" && tftRanks.isNotEmpty()) {
            tftRanks
        } else {
            (leagueRanks + tftRanks).sortedByDescending { it.score }
        }
    }

    private fun getTFTRanks(name: String, tag: String): List<RankResponse> {
        val puuid = accountAPI.getAccountByTag(leagueShard.toRegionShard(), name, tag, ApiKeyType.TFT)?.puuid ?: return emptyList()
        val summoner = tftAPI.summonerAPI.getSummonerByPUUID(leagueShard, puuid)
        val leagueEntries = tftAPI.leagueAPI.getLeagueEntriesByPUUID(leagueShard, summoner.puuid)
        return leagueEntries.filter { it.ratedTier == null }.map { entry ->
            val queueId = entry.queueType.values.firstOrNull() ?: Queue.RANKED_TFT.id
            RankResponse(
                tier = entry.tier.toLowerCase(),
                division = entry.rank,
                points = entry.leaguePoints,
                series = "",
                wins = entry.wins,
                leagueName = "",
                queueName = Queue.getTag(queueId),
                queueId = queueId,
            )
        }
    }

    private fun getLeagueRanks(id: String): List<RankResponse> {
        val summoner = leagueAPI.summonerAPI.getSummonerByPUUID(leagueShard, id)
        val leagueEntries = summoner.leagueEntry
        if (leagueEntries.isEmpty()) {
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
        return leagueEntries.filter { it.queueType != GameQueueType.CHERRY }.map { entry ->
            val series = if (entry.isInPromos) {
                "${entry.miniSeries.wins}W-${entry.miniSeries.losses}L"
            } else {
                ""
            }
            val leagueName = leagueAPI.leagueAPI.getLeague(leagueShard, entry.leagueId).leagueName
            val queueId = getQueueId(entry.queueType)
            RankResponse(
                tier = entry.tier.toLowerCase(),
                division = entry.rank,
                points = entry.leaguePoints,
                series = series,
                wins = entry.wins,
                leagueName = leagueName,
                queueName = Queue.getTag(queueId),
                queueId = queueId,
            )
        }
    }

    private fun getQueueId(queueType: GameQueueType): Int {
        val queue = Queue.getQueue(queueType.apiName)
        return if (queue != Queue.UNKNOWN) {
            queue.id
        } else {
            queueType.values.firstOrNull {
                Queue.getTag(it) != Queue.UNKNOWN.tag
            }
        } ?: Queue.UNKNOWN.id
    }

    override fun getTopChamps(id: String, limit: Int): TopChampsResponse {
        val summoner = leagueAPI.summonerAPI.getSummonerByPUUID(leagueShard, id)
        val topChamps = summoner.championMasteries.take(limit).map { championMastery ->
            TopChampResponse(
                championMastery.championId,
                "",
                championMastery.championLevel,
                championMastery.championPoints,
            )
        }
        return TopChampsResponse(topChamps)
    }

    override fun getChampName(id: Int): String {
        return champs.computeIfAbsent(id) {
            dDragonAPI.getChampion(id).name
        }
    }

    override fun getCurrentGame(id: String): CurrentGameResponse {
        val summoner = leagueAPI.summonerAPI.getSummonerByPUUID(leagueShard, id)
        if (summoner.currentGame == null) throw NotFoundException
        val summoners = summoner.currentGame.participants.map { participant ->
            val entries = leagueAPI.leagueAPI.getLeagueEntriesByPUUID(leagueShard, participant.puuid)
            val rankedEntry = entries.firstOrNull {
                it.queueType == GameQueueType.RANKED_SOLO_5X5
            }
            val entry = entries.firstOrNull {
                it.queueType == summoner.currentGame.gameQueueConfig
            } ?: rankedEntry
            val account = accountAPI.getAccountByPUUID(leagueShard.toRegionShard(), participant.puuid)
            val name = "${account.name}#${account.tag}"
            CurrentGameSummonerResponse(
                name = name,
                selected = participant.summonerId == id,
                tier = entry?.tier?.toLowerCase() ?: "unranked",
                division = entry?.rank ?: "",
                champId = participant.championId,
                teamId = participant.team.value,
            )
        }
        val queueId = getQueueId(summoner.currentGame.gameQueueConfig)
        return CurrentGameResponse(Queue.getTag(queueId), summoners)
    }

    override fun getMatch(id: Long): MatchResponse {
        val matchBuilder = MatchBuilder(leagueShard)
        val match = matchBuilder.withId("${leagueShard.value}_$id").match
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
            // This call fails if the participant is a bot
            val summoner = if (participant.puuid != "BOT") {
                Summoner.byPUUID(leagueShard, participant.puuid)
            } else {
                null
            }
            var entry = summoner?.leagueEntry?.firstOrNull { it.queueType == match.queue }
            if (entry == null) {
                entry = summoner?.leagueEntry?.firstOrNull { it.queueType == GameQueueType.RANKED_SOLO_5X5 }
            }
            val items = getItems(participant)
            // summoner spell is null for bots
            val spells = listOf(participant?.summoner1Id ?: 0, participant?.summoner2Id ?: 0)
            val badges = mutableListOf<Badge>()
            if (participant.team == TeamType.BLUE) {
                blueTeamGold += participant.goldEarned
                blueTeamKills += participant.kills
                blueTeamDeaths += participant.deaths
                blueTeamAssists += participant.assists
                blueTeamDamage += participant.totalDamageDealtToChampions
            } else {
                redTeamGold += participant.goldEarned
                redTeamKills += participant.kills
                redTeamDeaths += participant.deaths
                redTeamAssists += participant.assists
                redTeamDamage += participant.totalDamageDealtToChampions
            }
            if (participant.isFirstBloodKill) badges.add(Badge.FIRST_BLOOD)
            when {
                participant.pentaKills > 0 -> badges.add(Badge.PENTA_KILL)
                participant.quadraKills > 0 -> badges.add(Badge.QUADRA_KILL)
                participant.tripleKills > 0 -> badges.add(Badge.TRIPLE_KILL)
            }
            val name = "${participant.riotIdName}#${participant.riotIdTagline}"
            PlayerResponse(
                id = summoner?.puuid ?: participant.puuid,
                name = name,
                tier = entry?.tier ?: "unranked",
                division = entry?.tierDivisionType?.division ?: "",
                participantId = index + 1,
                teamId = participant.team.value,
                champId = participant.championId,
                champName = participant.championName,
                kills = participant.kills,
                deaths = participant.deaths,
                assists = participant.assists,
                gold = participant.goldEarned,
                damage = participant.totalDamageDealtToChampions,
                cs = participant.totalMinionsKilled + participant.neutralMinionsKilled,
                level = participant.championLevel,
                win = participant.didWin(),
                wards = participant.wardsPlaced,
                lane = participant.lane.name,
                role = participant.role.name,
                items = if (items.isNotEmpty()) items.subList(0, items.size - 1) else listOf(),
                trinket = items.lastOrNull()?.id ?: 0,
                spells = spells,
                // Perks are intermittently null.
                keystone = participant.perks.perkStyles.firstOrNull()?.selections?.firstOrNull()?.perk ?: 0,
                badges = badges,
            )
        }

        val matchBlueTeam = match.teams.first { it.teamId.value == 100 }
        val matchRedTeam = match.teams.first { it.teamId.value == 200 }
        val blueTeam = TeamResponse(
            id = matchBlueTeam.teamId.value,
            win = matchBlueTeam.didWin(),
            towers = matchBlueTeam.objectives["tower"]?.kills ?: 0,
            dragons = matchBlueTeam.objectives["dragon"]?.kills ?: 0,
            barons = matchBlueTeam.objectives["baron"]?.kills ?: 0,
            gold = blueTeamGold,
            kills = blueTeamKills,
            deaths = blueTeamDeaths,
            assists = blueTeamAssists,
            bans = matchBlueTeam.bans.map { it.championId }
        )

        val redTeam = TeamResponse(
            id = matchRedTeam.teamId.value,
            win = matchRedTeam.didWin(),
            towers = matchRedTeam.objectives["tower"]?.kills ?: 0,
            dragons = matchRedTeam.objectives["dragon"]?.kills ?: 0,
            barons = matchRedTeam.objectives["baron"]?.kills ?: 0,
            gold = redTeamGold,
            kills = redTeamKills,
            deaths = redTeamDeaths,
            assists = redTeamAssists,
            bans = matchRedTeam.bans.map { it.championId }
        )

        var maxKda: PlayerResponse? = null
        var maxWards: PlayerResponse? = null
        var maxDamage: PlayerResponse? = null
        var maxGold: PlayerResponse? = null
        players.forEach { player ->
            if (maxKda == null || LolByteUtils.computeKda(player) > LolByteUtils.computeKda(maxKda)) maxKda = player
            if (player.wards > (maxWards?.wards ?: 0)) maxWards = player
            if (player.damage > (maxDamage?.damage ?: 0)) maxDamage = player
            if (player.gold > (maxGold?.gold ?: 0)) maxGold = player
            if (player.teamId == TeamType.BLUE.value) {
                player.damageContribution = LolByteUtils.divideInts(player.damage, blueTeamDamage)
                player.killParticipation = LolByteUtils.divideInts(player.kills + player.assists, blueTeamKills)
            } else {
                player.damageContribution = LolByteUtils.divideInts(player.damage, redTeamDamage)
                player.killParticipation = LolByteUtils.divideInts(player.kills + player.assists, redTeamKills)
            }
        }

        maxKda?.badges?.add(Badge.HIGHEST_KDA)
        maxWards?.badges?.add(Badge.MOST_WARDS)
        maxDamage?.badges?.add(Badge.MOST_DAMAGE)
        maxGold?.badges?.add(Badge.MOST_GOLD)

        return MatchResponse(
            id = match.gameId,
            queueName = Queue.getTag(match.queue.values.firstOrNull()),
            duration = match.gameDurationAsDuration.toMinutes(),
            timestamp = match.gameStartTimestamp,
            blueTeam = blueTeam,
            redTeam = redTeam,
            players = players.sortedBy { it.order },
        )
    }

    companion object {
        private val defaultGameQueueTypes = listOf(
            GameQueueType.TEAM_BUILDER_DRAFT_UNRANKED_5X5,
            GameQueueType.TEAM_BUILDER_RANKED_SOLO,
            GameQueueType.RANKED_FLEX_SR,
            GameQueueType.ARAM
        )
        private val rankedGameQueueTypes = listOf(GameQueueType.TEAM_BUILDER_RANKED_SOLO, GameQueueType.RANKED_FLEX_SR)
        private val clashGameQueueTypes = listOf(GameQueueType.CLASH)
    }
}
