package com.badger.lolbyte.client

import com.badger.lolbyte.config.CacheProperties
import com.badger.lolbyte.summoner.SummonerResponse
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.RuntimeException

class TestCachingClient {
    private val cacheProperties = CacheProperties(
        summonerTtl = 60,
        recentGamesTtl = 60,
        ranksTtl = 60,
        topChampsTtl = 60,
        champTtl = 60,
        matchTtl = 60
    )

    @Test
    fun testCachingClient() {
        val baseClient = TestClient(summonerResponse = SummonerResponse("id", "na", "erelor", 69, 420))
        val cachingClient = baseClient.withCaching(cacheProperties)
        Assertions.assertEquals(baseClient.getSummoner("erelor"), cachingClient.getSummoner("erelor"))
    }

    @Test
    fun textExceptions() {
        val baseClient = object : TestClient() {
            override fun getSummoner(name: String): SummonerResponse {
                throw RuntimeException("error!")
            }
        }
        val cachingClient = baseClient.withCaching(cacheProperties)
        assertThrows<RuntimeException> {
            baseClient.getSummoner("")
        }
        assertThrows<RuntimeException> {
            cachingClient.getSummoner("")
        }
    }
}
