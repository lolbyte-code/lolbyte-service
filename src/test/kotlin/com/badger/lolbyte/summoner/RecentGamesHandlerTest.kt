package com.badger.lolbyte.summoner

import com.badger.lolbyte.client.TestClient
import com.badger.lolbyte.recent.ItemResponse
import com.badger.lolbyte.recent.RecentGameResponse
import com.badger.lolbyte.recent.RecentGamesHandler
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class RecentGamesHandlerTest {
    private val testResponse = RecentGameResponse(
        id = 123,
        timestamp = 123L,
        teamId = 100,
        champId = 10,
        win = true,
        kills = 1,
        deaths = 2,
        assists = 3,
        wards = 4,
        cs = 5,
        queueName = "Queue",
        duration = 100L,
        items = listOf(ItemResponse(123, "Doran's Blade", "Best weapon ever")),
        spells = listOf(1, 2),
        keystone = 3
    )
    private val handler = RecentGamesHandler(TestClient(recentGamesResponse = listOf(testResponse)))

    @Test
    fun testGetRecentGames() {
        val response = handler.getRecentGames("123", 10)
        Assertions.assertEquals(1, response.total)
        Assertions.assertEquals(1, response.data.size)
        Assertions.assertEquals(testResponse, response.data[0])
    }
}
