package com.badger.lolbyte.summoner

import com.badger.lolbyte.client.TestClient
import com.badger.lolbyte.util.Region
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.UUID

class SummonerHandlerTest {
    private val testResponse = SummonerResponse(
        UUID.randomUUID().toString(),
        Region.NORTH_AMERICA.region,
        "foo",
        420,
        69
    )

    private val handler = SummonerHandler(TestClient(summonerResponse = testResponse))

    @Test
    fun testGetSummoner() {
        val response = handler.getSummoner("foo")
        Assertions.assertEquals(testResponse, response)
    }
}
