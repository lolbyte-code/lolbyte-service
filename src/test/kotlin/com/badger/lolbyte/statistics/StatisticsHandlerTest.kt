package com.badger.lolbyte.statistics

import com.badger.lolbyte.client.TestClient
import com.badger.lolbyte.recent.ItemResponse
import com.badger.lolbyte.recent.RecentGameResponse
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class StatisticsHandlerTest {
    private val testResponse = listOf(
        RecentGameResponse(
            id = 123,
            timestamp = 123L,
            teamId = 100,
            champId = 10,
            win = true,
            kills = 1,
            deaths = 2,
            assists = 3,
            wards = 24,
            cs = 115,
            queueName = "Queue",
            duration = 100L,
            items = listOf(ItemResponse(123, "Doran's Blade", "Best weapon ever")),
            spells = listOf(1, 2),
            keystone = 3
        ),
        RecentGameResponse(
            id = 456,
            timestamp = 123L,
            teamId = 100,
            champId = 10,
            win = true,
            kills = 12,
            deaths = 24,
            assists = 32,
            wards = 43,
            cs = 500,
            queueName = "Queue",
            duration = 100L,
            items = listOf(ItemResponse(123, "Doran's Blade", "Best weapon ever")),
            spells = listOf(1, 2),
            keystone = 3
        ),
        RecentGameResponse(
            id = 789,
            timestamp = 123L,
            teamId = 100,
            champId = 10,
            win = false,
            kills = 10,
            deaths = 22,
            assists = 3,
            wards = 41,
            cs = 522,
            queueName = "Queue",
            duration = 100L,
            items = listOf(ItemResponse(123, "Doran's Blade", "Best weapon ever")),
            spells = listOf(1, 2),
            keystone = 3
        )
    )
    private val handler = StatisticsHandler(TestClient(recentGamesResponse = testResponse))

    @Test
    fun testGetStatistics() {
        val response = handler.getStatistics("123", 3)
        val expected = PlayerStatsResponse("Last 3 Matches", 66, 7.666666666666667, 16.0, 12.666666666666666, 36.0)
        Assertions.assertEquals(1, response.playerStats.size)
        Assertions.assertEquals(expected, response.playerStats[0])
    }
}
