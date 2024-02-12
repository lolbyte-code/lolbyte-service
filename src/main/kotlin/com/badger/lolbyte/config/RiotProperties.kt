package com.badger.lolbyte.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "riot")
data class RiotProperties(val leagueApiKey: String, val tftApiKey: String)
