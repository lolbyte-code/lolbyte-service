package com.badger.lolbyte.client

import com.badger.lolbyte.config.RetryProperties
import com.badger.lolbyte.summoner.SummonerResponse
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.NullPointerException
import kotlin.RuntimeException

class TestRetryClient {
    private val retryProperties = RetryProperties(
        intervalInSeconds = 1,
        attempts = 3
    )

    @Test
    fun `verify base client and retry client output are the same`() {
        val baseClient = TestClient(summonerResponse = SummonerResponse("id", "na", "erelor", 69, 420))
        val retryClient = baseClient.withRetry(retryProperties)
        Assertions.assertEquals(baseClient.getSummoner("erelor"), retryClient.getSummoner("erelor"))
    }

    @Test
    fun `verify retry client throws exception after attempts are exceeded`() {
        val baseClient = object : TestClient() {
            override fun getSummoner(name: String): SummonerResponse {
                throw RuntimeException("error!")
            }
        }
        val retryClient = baseClient.withRetry(retryProperties)

        assertThrows<RuntimeException> {
            retryClient.getSummoner("erelor")
        }
    }

    @Test
    fun `verify retry client retries and returns successful result if attempts are not exceeded`() {
        val successfulResponse = SummonerResponse("id", "na", "erelor", 69, 420)
        val baseClient = object : TestClient() {
            var attemptsToSuccess = 0

            override fun getSummoner(name: String): SummonerResponse {
                return if (attemptsToSuccess++ < retryProperties.attempts) {
                    throw NullPointerException("error!")
                } else {
                    successfulResponse
                }
            }
        }
        val retryClient = baseClient.withRetry(retryProperties)

        assertThrows<RuntimeException> {
            baseClient.getSummoner("erelor")
        }

        Assertions.assertEquals(successfulResponse, retryClient.getSummoner(successfulResponse.name))
    }
}
