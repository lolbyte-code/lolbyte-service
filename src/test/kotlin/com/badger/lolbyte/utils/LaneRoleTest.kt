package com.badger.lolbyte.utils

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class LaneRoleTest {
    @Test
    fun testGetOrder() {
        Assertions.assertEquals(1, LaneRole.getOrder("BLAH", "BLAH"))
        LaneRole.values().forEach { laneRole ->
            Assertions.assertEquals(laneRole.order, LaneRole.getOrder(laneRole.lane, laneRole.role))
        }
    }
}
