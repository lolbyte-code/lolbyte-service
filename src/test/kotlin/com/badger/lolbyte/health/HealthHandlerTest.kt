package com.badger.lolbyte.health

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class HealthHandlerTest {
    @Test
    fun testGetHealth() {
        Assertions.assertEquals(true, HealthHandler.getHealth().healthy)
    }
}
