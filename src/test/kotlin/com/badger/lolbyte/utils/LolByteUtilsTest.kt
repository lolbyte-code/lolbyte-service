package com.badger.lolbyte.utils

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class LolByteUtilsTest {
    @Test
    fun testGenerateRank() {
        Assertions.assertEquals("Challenger I", LolByteUtils.generateRank("cHalleNgER", "I"))
        Assertions.assertEquals("Gold IV", LolByteUtils.generateRank("gold", "IV"))
        Assertions.assertEquals("Silver II", LolByteUtils.generateRank("SILVER", "II"))
    }
}
