package com.badger.lolbyte.region

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class RegionTest {
    @Test
    fun `test fromString`() {
        Assertions.assertEquals(Region.NORTH_AMERICA, Region.fromString("na"))
        Assertions.assertEquals(Region.NORTH_AMERICA, Region.fromString("NA"))
        Assertions.assertEquals(Region.NORTH_AMERICA, Region.fromString(""))
        Assertions.assertEquals(Region.NORTH_AMERICA, Region.fromString(null))
        Assertions.assertEquals(Region.BRAZIL, Region.fromString("br"))
        Assertions.assertEquals(Region.EUROPE_NORTH_EAST, Region.fromString("eune"))
        Assertions.assertEquals(Region.EUROPE_WEST, Region.fromString("euw"))
        Assertions.assertEquals(Region.JAPAN, Region.fromString("jp"))
        Assertions.assertEquals(Region.KOREA, Region.fromString("kr"))
        Assertions.assertEquals(Region.LATIN_AMERICA_NORTH, Region.fromString("lan"))
        Assertions.assertEquals(Region.LATIN_AMERICA_SOUTH, Region.fromString("las"))
        Assertions.assertEquals(Region.OCEANIA, Region.fromString("oce"))
        Assertions.assertEquals(Region.RUSSIA, Region.fromString("ru"))
        Assertions.assertEquals(Region.TURKEY, Region.fromString("tr"))
    }
}
