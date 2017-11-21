package com.scherule.users.swagger

import com.google.common.base.Predicate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.service.ApiInfo
import springfox.documentation.schema.ModelRef
import springfox.documentation.builders.ResponseMessageBuilder
import org.springframework.web.bind.annotation.RequestMethod
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.builders.PathSelectors.regex


@Configuration
@Profile("docs")
class SwaggerConfig {

    @Bean
    fun swaggerApi(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
                .pathMapping("/api")
                .groupName("scherule-users")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(getSwaggerPaths())
                .build()
    }

    private fun apiInfo(): ApiInfo {
        return ApiInfoBuilder()
                .title("Spring Demo with Swagger")
                .description("Spring Demo with Swagger")
                .contact(Contact("Grzegorz Gurgul",
                        "http://gibhub.com/kboom",
                        "gurgul.grzegorz@gmail.com"))
                .license("License name here")
                .licenseUrl("URL to license")
                .version("1.0.1")
                .build()
    }

    private fun getSwaggerPaths(): Predicate<String> {
        return regex("/api.*");
    }

}