package com.badger.lolbyte.match

import com.badger.lolbyte.client.TestClient
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class MatchHandlerTest {
    private val testResponse = MatchResponse(
        id = 123,
        blueTeam = TeamResponse(
            id = 100,
            win = true,
            towers = 1,
            dragons = 2,
            barons = 3,
            gold = 100,
            kills = 45,
            deaths = 10,
            assists = 2,
            bans = listOf(10, 11, 12)
        ),
        redTeam = TeamResponse(
            id = 200,
            win = false,
            towers = 1,
            dragons = 2,
            barons = 3,
            gold = 100,
            kills = 45,
            deaths = 10,
            assists = 2,
            bans = listOf(13, 14, 15)
        ),
        players = listOf(
            PlayerResponse(
                id = "456",
                name = "tyler1",
                tier = "Challenger",
                division = "I",
                participantId = 1,
                teamId = 100,
                champId = 69,
                champName = "Draven",
                kills = 100,
                deaths = 0,
                assists = 0,
                gold = 42000,
                damage = 9001,
                cs = 2,
                level = 18,
                win = true,
                wards = 2,
                lane = "BOT",
                role = "MARKSMAN",
                items = listOf(ItemResponse(1, "Triforce", "Tons of dmg")),
                trinket = 6,
                spells = listOf(1, 2),
                keystone = 22,
                badges = mutableListOf(Badge.FIRST_BLOOD, Badge.PENTA_KILL, Badge.HIGHEST_KDA, Badge.MOST_GOLD, Badge.MOST_DAMAGE)
            )
        )
    )
    private val handler = MatchHandler(TestClient(matchResponse = testResponse))

    @Test
    fun testGetMatch() {
        val response = handler.getMatch(123, "456")
        Assertions.assertEquals(testResponse, response)
    }
}
