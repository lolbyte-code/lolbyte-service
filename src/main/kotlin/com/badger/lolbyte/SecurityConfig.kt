package com.badger.lolbyte

import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration

@EnableWebSecurity
class SecurityConfig {
    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        val corsConfiguration = CorsConfiguration()
        corsConfiguration.allowedHeaders = listOf("Authorization", "Cache-Control", "Content-Type")
        corsConfiguration.allowedOriginPatterns = listOf("*")
        corsConfiguration.allowedMethods =
            listOf("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
        corsConfiguration.allowCredentials = true
        corsConfiguration.exposedHeaders = listOf("Authorization")

        http.authorizeRequests().antMatchers("/**").permitAll().anyRequest()
            .authenticated().and().csrf().disable().cors().configurationSource { _ -> corsConfiguration }

        return http.build()
    }
}
