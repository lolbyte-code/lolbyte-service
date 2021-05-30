package com.badger.lolbyte

import com.badger.lolbyte.config.RiotProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(RiotProperties::class)
class LolByteApplication

fun main(args: Array<String>) {
    runApplication<LolByteApplication>(*args)
}
