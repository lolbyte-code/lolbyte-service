package com.badger.lolbyte.utils

import org.cache2k.Cache
import org.cache2k.Cache2kBuilder
import java.util.concurrent.TimeUnit

object Cache {
    const val ETERNAL_TTL = -1L

    inline fun <reified K, reified V> buildCache(expirationMinutes: Long): Cache<K, V> {
        val builder = Cache2kBuilder.of(K::class.java, V::class.java)
        if (expirationMinutes < 0) {
            builder.eternal(true)
        } else {
            builder.expireAfterWrite(expirationMinutes, TimeUnit.MINUTES)
        }
        return builder.build()
    }
}
