package com.badger.lolbyte.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "retry")
data class RetryProperties(val intervalInSeconds: Long, val attempts: Int)
