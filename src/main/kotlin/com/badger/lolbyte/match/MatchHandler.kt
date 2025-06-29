package com.badger.lolbyte.match

import com.badger.lolbyte.client.LeagueApiClient
import com.badger.lolbyte.utils.LaneRole
import com.badger.lolbyte.utils.LolByteUtils

data class MatchResponse(
    val id: Long,
    val queueName: String,
    val duration: Long,
    val timestamp: Long,
    val blueTeam: TeamResponse,
    val redTeam: TeamResponse,
    val players: List<PlayerResponse>,
)

data class PlayerResponse(
    val id: String,
    val name: String,
    val rank: String,
    val participantId: Int,
    val teamId: Int,
    val champId: Int,
    val champName: String,
    val kills: Int,
    val deaths: Int,
    val assists: Int,
    val gold: Int,
    val damage: Int,
    val cs: Int,
    val level: Int,
    val win: Boolean,
    val wards: Int,
    val order: Int,
    val lane: String,
    val role: String,
    val items: List<ItemResponse>,
    val trinket: Int,
    val spells: List<Int>,
    val keystone: Int,
    var killParticipation: Int,
    var damageContribution: Int,
    val badges: MutableList<Badge>,
) {
    constructor(
        id: String,
        name: String,
        tier: String,
        division: String,
        participantId: Int,
        teamId: Int,
        champId: Int,
        champName: String,
        kills: Int,
        deaths: Int,
        assists: Int,
        gold: Int,
        damage: Int,
        cs: Int,
        level: Int,
        win: Boolean,
        wards: Int,
        lane: String,
        role: String,
        items: List<ItemResponse>,
        trinket: Int,
        spells: List<Int>,
        keystone: Int,
        badges: MutableList<Badge>,
    ) : this(
        id = id,
        name = name,
        rank = LolByteUtils.generateRank(tier, division),
        participantId = participantId,
        teamId = teamId,
        champId = champId,
        champName = champName,
        kills = kills,
        deaths = deaths,
        assists = assists,
        gold = gold,
        damage = damage,
        cs = cs,
        level = level,
        win = win,
        wards = wards,
        order = ("${(teamId / 100)}${LaneRole.getOrder(lane, role)}").toInt(),
        lane = lane,
        role = role,
        items = items,
        trinket = trinket,
        spells = spells,
        keystone = keystone,
        killParticipation = 0,
        damageContribution = 0,
        badges = badges,
    )
}

data class ItemResponse(
    val id: Int,
    val name: String,
    val desc: String
)

enum class Badge {
    FIRST_BLOOD,
    PENTA_KILL,
    QUADRA_KILL,
    TRIPLE_KILL,
    HIGHEST_KDA,
    MOST_WARDS,
    MOST_DAMAGE,
    MOST_GOLD;
}

data class TeamResponse(
    val id: Int,
    val win: Boolean,
    val towers: Int,
    val dragons: Int,
    val barons: Int,
    val gold: Int,
    val kills: Int,
    val deaths: Int,
    val assists: Int,
    val bans: List<Int>,
)

class MatchHandler(private val client: LeagueApiClient) {
    fun getMatch(id: Long): MatchResponse {
        return client.getMatch(id)
    }
}
