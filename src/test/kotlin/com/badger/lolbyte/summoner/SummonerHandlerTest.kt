package com.badger.lolbyte.summoner

import com.badger.lolbyte.client.TestClient
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class SummonerHandlerTest {
    private val handler = SummonerHandler(TestClient)

    @Test
    fun testGetSummoner() {
        val response = handler.getSummoner("foo")
        Assertions.assertEquals("foo", response.name)
        Assertions.assertEquals("na", response.region)
        Assertions.assertEquals(420, response.level)
        Assertions.assertEquals(69, response.icon)
    }
}
