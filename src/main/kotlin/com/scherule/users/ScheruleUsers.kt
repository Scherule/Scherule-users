package com.scherule.users

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.data.repository.query.spi.EvaluationContextExtension
import org.springframework.data.repository.query.spi.EvaluationContextExtensionSupport
import org.springframework.data.web.PageableHandlerMethodArgumentResolver
import org.springframework.data.web.config.EnableSpringDataWebSupport
import org.springframework.hateoas.config.EnableEntityLinks
import org.springframework.http.MediaType
import org.springframework.mail.MailSender
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.security.access.expression.SecurityExpressionRoot
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import springfox.documentation.swagger2.annotations.EnableSwagger2
import java.security.SecureRandom
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Implement internal user mechanisms https://gigsterous.github.io/engineering/2017/03/01/spring-boot-4.html
 *
 * http://www.swisspush.org/security/2016/10/17/oauth2-in-depth-introduction-for-enterprises
 */
@SpringBootApplication
@EnableEurekaClient
@EnableEntityLinks
@RestController
@EnableSpringDataWebSupport
@EnableSwagger2
class ScheruleUsers : WebMvcConfigurerAdapter() {

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry
                .addResourceHandler("/assets/**")
                .addResourceLocations("classpath:/static/assets/");
    }

    /**
     * Match frontend endpoints to get rid of # in the UI URLs
     */
    @RequestMapping(method = arrayOf(RequestMethod.GET), path = arrayOf(
            "/login*",
            "/register*",
            "/welcome*"
    ))
    fun redirect(request: HttpServletRequest, response: HttpServletResponse) {
        request.getRequestDispatcher("/").forward(request, response)
    }

    override fun addArgumentResolvers(argumentResolvers: MutableList<HandlerMethodArgumentResolver>) {
        val resolver = PageableHandlerMethodArgumentResolver()
        resolver.setMaxPageSize(100);
//        resolver.setOneIndexedParameters(true);
        argumentResolvers.add(resolver);
        super.addArgumentResolvers(argumentResolvers);
    }

    @Bean
    fun securityExtension(): EvaluationContextExtension {
        return SecurityEvaluationContextExtension()
    }

    override fun addViewControllers(registry: ViewControllerRegistry) {
        registry.addViewController("/").setViewName("forward:/index.html")
    }

    @Bean
    fun random() = SecureRandom()

    @Bean
    fun mailSender(
            @Value("\${mail.host}") host: String,
            @Value("\${mail.account.username}") username: String,
            @Value("\${mail.account.password}") password: String
    ): MailSender {
        val sender = JavaMailSenderImpl()
        sender.host = host
        sender.username = username
        sender.password = password

        sender.testConnection()

        return sender
    }

    @Bean
    fun registrationEmailMessage(
            @Value("\${registration.email.subject}") subject: String,
            @Value("\${registration.email.from}") from: String
    ): SimpleMailMessage {
        val message = SimpleMailMessage()
        message.from = from
        message.subject = subject
        return message
    }

    @Bean
    fun passwordResetEmailMessage(
            @Value("\${user.password.reset.email.subject}") subject: String,
            @Value("\${user.password.reset.email.from}") from: String
    ): SimpleMailMessage {
        val message = SimpleMailMessage()
        message.from = from
        message.subject = subject
        return message
    }

    @Bean
    fun corsFilter(): CorsFilter {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.allowCredentials = true
        config.addAllowedOrigin("*")
        config.addAllowedHeader("*")
        config.addAllowedMethod("*")
        source.registerCorsConfiguration("/**", config)
        return CorsFilter(source)
    }

    @Bean
    fun registerCorsFilter(filter: CorsFilter): FilterRegistrationBean {
        val reg = FilterRegistrationBean(filter)
        reg.order = Ordered.HIGHEST_PRECEDENCE
        return reg
    }

    internal inner class SecurityEvaluationContextExtension : EvaluationContextExtensionSupport() {

        override fun getExtensionId(): String {
            return "security"
        }

        override fun getRootObject(): SecurityExpressionRoot {
            val authentication = SecurityContextHolder.getContext().authentication
            return object : SecurityExpressionRoot(authentication) {}
        }
    }


    @Configuration
    class WebConfig : WebMvcConfigurerAdapter() {

        override fun configureContentNegotiation(configurer: ContentNegotiationConfigurer?) {
            configurer!!.favorPathExtension(false)
                    .favorParameter(true)
                    .defaultContentType(MediaType.APPLICATION_JSON)
                    .mediaType("xml", MediaType.APPLICATION_XML)
        }

    }

}

fun main(args: Array<String>) {
    SpringApplication.run(ScheruleUsers::class.java, *args)
}


