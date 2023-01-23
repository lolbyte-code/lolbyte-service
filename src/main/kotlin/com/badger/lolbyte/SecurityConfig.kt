package com.badger.lolbyte

import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.web.cors.CorsConfiguration

@EnableWebSecurity
class SecurityConfig : WebSecurityConfigurerAdapter() {
    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        val corsConfiguration = CorsConfiguration()
        corsConfiguration.allowedHeaders = listOf("Authorization", "Cache-Control", "Content-Type")
        corsConfiguration.allowedOriginPatterns = listOf("*")
        corsConfiguration.allowedMethods =
            listOf("GET", "POST", "PUT", "DELETE", "PUT", "OPTIONS", "PATCH", "DELETE")
        corsConfiguration.allowCredentials = true
        corsConfiguration.exposedHeaders = listOf("Authorization")

        http.authorizeRequests().antMatchers("/**").permitAll().anyRequest()
            .authenticated().and().csrf().disable().cors().configurationSource { request -> corsConfiguration }
    }
}
