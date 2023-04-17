package com.badger.lolbyte.utils

import java.lang.NullPointerException
import kotlin.reflect.KClass

object RetryException : RuntimeException("Retry fatal error!")

class Retrier(private val intervalInSeconds: Long, private val attempts: Int) {
    fun <R> withRetry(exceptions: List<KClass<*>> = listOf(NullPointerException::class), block: () -> R): R {
        var attempt = 1
        while (attempt <= attempts + 1) {
            try {
                return block.invoke()
            } catch (e: Exception) {
                if (!exceptions.contains(e::class) || attempt > attempts) {
                    throw e
                }
                Thread.sleep(intervalInSeconds * 1000)
            }
            attempt++
        }
        throw RetryException
    }
}
