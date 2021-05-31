package com.badger.lolbyte.rank

import com.badger.lolbyte.client.TestClient
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class RankHandlerTest {
    private val testResponse = RankResponse(
        tier = "GOLD",
        division = "II",
        points = 12,
        wins = 100,
        leagueName = "Tony's Tigers",
        queueName = "Solo Q",
        queueId = 420
    )
    private val handler = RanksHandler(TestClient(ranksResponse = listOf(testResponse)))

    @Test
    fun testGetRanks() {
        val response = handler.getRanks("123")
        Assertions.assertEquals(1, response.total)
        Assertions.assertEquals(1, response.data.size)
        Assertions.assertEquals(testResponse, response.data[0])
    }
}
