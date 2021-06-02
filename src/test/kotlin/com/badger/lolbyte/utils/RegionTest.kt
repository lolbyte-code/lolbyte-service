package com.badger.lolbyte.utils

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class RegionTest {
    @Test
    fun `test fromString`() {
        Assertions.assertEquals(Region.NORTH_AMERICA, Region.fromString(""))
        Assertions.assertEquals(Region.NORTH_AMERICA, Region.fromString("foo"))
        Assertions.assertEquals(Region.NORTH_AMERICA, Region.fromString(null))
        Region.values().forEach { region ->
            Assertions.assertEquals(region, Region.fromString(region.region))
            Assertions.assertEquals(region, Region.fromString(region.region.toUpperCase()))
        }
    }
}
