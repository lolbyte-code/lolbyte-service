package com.badger.lolbyte.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket

@Configuration
class SpringFoxConfig {
    @Bean
    fun api(): Docket {
        return Docket(DocumentationType.OAS_30)
            .select()
            .apis(RequestHandlerSelectors.any())
            .paths(PathSelectors.any())
            .build()
            .apiInfo(apiInfo())
    }
}

private fun apiInfo(): ApiInfo {
    return ApiInfo(
        "LolByte Service API",
        "",
        "",
        "",
        ApiInfo.DEFAULT_CONTACT,
        "",
        "",
        emptyList()
    )
}
