package com.badger.lolbyte.statistics

import com.badger.lolbyte.client.TestClient
import com.badger.lolbyte.match.ItemResponse
import com.badger.lolbyte.recent.RecentGameResponse
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class StatisticsHandlerTest {
    private val testGamesResponse = listOf(
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
            champId = 11,
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
            champId = 12,
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
    private val testTopChampsResponse = TopChampsResponse(
        listOf(
            TopChampResponse(10, "Shaco", 7, 100),
            TopChampResponse(11, "Zed", 7, 300),
            TopChampResponse(12, "Lux", 7, 400),
        )
    )
    private val testChampMapping = mapOf(10 to "Shaco", 11 to "Zed", 12 to "Lux")
    private val handler = StatisticsHandler(
        TestClient(
            recentGamesResponse = testGamesResponse,
            topChampsResponse = testTopChampsResponse,
            champMapping = testChampMapping
        )
    )

    @Test
    fun testGetStatistics() {
        val response = handler.getStatistics("123", 3, null)
        Assertions.assertEquals(3, response.statistics.size)
        response.statistics.forEach { statistic ->
            val expected = when (statistic) {
                is PlayerStatsResponse -> PlayerStatsResponse(
                    type = "Last 3 Matches",
                    winPercentage = 66,
                    kills = 7.666666666666667,
                    deaths = 16.0,
                    assists = 12.666666666666666,
                    wards = 36.0
                )
                is MostPlayedChampsResponse -> MostPlayedChampsResponse(
                    listOf(
                        MostPlayedChampResponse(10, "Shaco", 1),
                        MostPlayedChampResponse(11, "Zed", 1),
                        MostPlayedChampResponse(12, "Lux", 1)
                    )
                )
                is TopChampsResponse -> testTopChampsResponse
                else -> Assertions.fail("Unexpected statistics response")
            }
            Assertions.assertEquals(expected, statistic)
        }
    }
}
