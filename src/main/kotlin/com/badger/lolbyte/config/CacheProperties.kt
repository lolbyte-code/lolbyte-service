package com.badger.lolbyte.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "cache")
data class CacheProperties(
    val summonerTtl: Long,
    val recentGamesTtl: Long,
    val ranksTtl: Long,
    val topChampsTtl: Long,
    val champTtl: Long,
    val matchTtl: Long,
)
