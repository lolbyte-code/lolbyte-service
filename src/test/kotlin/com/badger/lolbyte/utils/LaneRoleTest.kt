package com.badger.lolbyte.utils

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class LaneRoleTest {
    @Test
    fun testGetOrder() {
        Assertions.assertEquals(1, LaneRole.getOrder("BLAH", "BLAH"))
        Assertions.assertEquals(1, LaneRole.getOrder("TOP", ""))
        Assertions.assertEquals(2, LaneRole.getOrder("JUNGLE", ""))
        Assertions.assertEquals(3, LaneRole.getOrder("MIDDLE", ""))
        Assertions.assertEquals(4, LaneRole.getOrder("BOTTOM", "DUO_CARRY"))
        Assertions.assertEquals(5, LaneRole.getOrder("BOTTOM", "DUO_SUPPORT"))
    }
}
