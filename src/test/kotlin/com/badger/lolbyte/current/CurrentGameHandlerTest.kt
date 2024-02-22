package com.badger.lolbyte.current

import com.badger.lolbyte.client.TestClient
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class CurrentGameHandlerTest {
    private val testResponse = CurrentGameResponse(
        "Solo Q",
        listOf(
            SummonerResponse(
                "foo",
                true,
                "Master I",
                10,
                100,
            ),
            SummonerResponse(
                "bar",
                false,
                "Challenger I",
                11,
                200,
            )
        )
    )
    private val handler = CurrentGameHandler(TestClient(currentGameResponse = testResponse))

    @Test
    fun testGetCurrentGame() {
        val response = handler.getCurrentGame("123", false)
        Assertions.assertEquals(testResponse, response)
    }
}
