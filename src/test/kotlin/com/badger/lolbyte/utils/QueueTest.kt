package com.badger.lolbyte.utils

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class QueueTest {
    @Test
    fun testGetTag() {
        Assertions.assertEquals("Unknown", Queue.getTag(null))
        Assertions.assertEquals("Unknown", Queue.getTag(2934924))
        Queue.values().forEach { queue ->
            Assertions.assertEquals(queue.tag, Queue.getTag(queue.id))
        }
    }
}
