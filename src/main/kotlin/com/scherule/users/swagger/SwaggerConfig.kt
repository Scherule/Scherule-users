package com.scherule.users.swagger

import com.google.common.base.Predicate
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import springfox.documentation.schema.ModelRef
import org.springframework.web.bind.annotation.RequestMethod
import springfox.documentation.builders.*
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.builders.PathSelectors.regex
import springfox.documentation.service.*
import springfox.documentation.spi.service.contexts.SecurityContext
import springfox.documentation.swagger.web.ApiKeyVehicle
import springfox.documentation.swagger.web.SecurityConfiguration
import springfox.documentation.service.SecurityReference




/**
 * https://swagger.io/docs/specification/authentication/cookie-authentication/
 */
@Configuration
@Profile("docs")
class SwaggerConfig(
        @Value("\${server.session.cookie.name}") private val sessionCookieName: String
) {

    @Bean
    fun swaggerApi(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts())
                .groupName("scherule-users")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(getSwaggerPaths())
                .build()
    }

    private fun securitySchemes() = listOf(
            ApiKey("Session", sessionCookieName, "cookie"),
            BasicAuth("Local"),
            OAuthBuilder().name("Remote").build()
    )

    private fun securityContexts() = listOf(
            SecurityContext.builder()
                    .securityReferences(defaultAuth())
                    .forPaths(PathSelectors.any())
                    .build()
    )

    fun defaultAuth(): List<SecurityReference> {
        val authorizationScope = AuthorizationScope("global", "accessEverything")
        val authorizationScopes = arrayOfNulls<AuthorizationScope>(1)
        authorizationScopes[0] = authorizationScope
        return listOf(SecurityReference("Session", authorizationScopes))
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