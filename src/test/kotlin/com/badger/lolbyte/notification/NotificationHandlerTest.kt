package com.badger.lolbyte.notification

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class NotificationHandlerTest {
    private val handler = NotificationHandler

    @Test
    fun testGetNotification() {
        Assertions.assertEquals("nah", handler.getNotification().alert)
    }
}
