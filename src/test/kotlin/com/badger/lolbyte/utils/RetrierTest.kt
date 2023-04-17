package com.badger.lolbyte.utils

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class RetrierTest {
    @Test
    fun `validate good path`() {
        val expectedValue = 12
        val retrier = Retrier(0, 3)
        val testClass = TestClass(2, expectedValue)
        Assertions.assertEquals(expectedValue, retrier.withRetry { testClass.foo() })
    }

    @Test
    fun `validate bad path`() {
        val expectedValue = 12
        val retrier = Retrier(0, 3)
        val testClass = TestClass(4, expectedValue)
        assertThrows<NullPointerException> {
            retrier.withRetry { testClass.foo() }
        }
    }

    class TestClass(private val failures: Int, private val value: Int) {
        private var attempt = 1

        fun foo(): Int {
            if (attempt++ <= failures) {
                throw NullPointerException()
            }
            return value
        }
    }
}
