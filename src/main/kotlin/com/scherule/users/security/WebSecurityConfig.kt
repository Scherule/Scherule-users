package com.scherule.users.security

import com.scherule.users.domain.models.AuthorityName
import com.scherule.users.domain.models.IdentityType
import com.scherule.users.domain.models.PredefinedUserPrincipal
import com.scherule.users.domain.models.UserPrincipal
import com.scherule.users.domain.services.UserIdentityBinder
import com.scherule.users.handlers.LocalAuthenticationFailureHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.SecurityProperties
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.oauth2.client.OAuth2ClientContext
import org.springframework.security.oauth2.client.OAuth2RestTemplate
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.AnyRequestMatcher
import org.springframework.web.filter.CompositeFilter
import java.util.*
import javax.servlet.Filter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

// todo try this https://stackoverflow.com/questions/28908946/spring-security-oauth2-and-form-login-configuration
@Configuration
@EnableOAuth2Client
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
class WebSecurityConfig : WebSecurityConfigurerAdapter() {

    @Autowired
    private lateinit var oauth2ClientContext: OAuth2ClientContext

    @Autowired
    private lateinit var userDetailsService: UserDetailsService

    @Autowired
    private lateinit var eventPublisher: ApplicationEventPublisher

    @Autowired
    private lateinit var userIdentityBinder: UserIdentityBinder

    @Throws(Exception::class)
    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder())
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http
                .formLogin()
                .loginProcessingUrl("/api/login")
                .failureHandler(localAuthenticationFailureHandler())
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET,
                        "/",
                        "/login",
                        "/register",
                        "/assets/**",
                        "/webjars/**",
                        "/health/**",
                        "/swagger*/**",
                        "/**/api-docs/**"
                ).permitAll()
                .antMatchers(
                        HttpMethod.POST,
                        "/api/login**",
                        "/api/registration**",
                        "/api/registration/confirmation**",
                        "/api/user/password/reset*/**"
                ).permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(
                        DelegatingAuthenticationEntryPoint(
                                linkedMapOf(
                                    AntPathRequestMatcher("/api/**") to UnauthorizedMultipleMethodsEntryPoint("scherule"),
                                    AnyRequestMatcher.INSTANCE to LoginUrlAuthenticationEntryPoint("/login")
                                )
                        )
                )
                .and().logout().logoutSuccessUrl("/").permitAll().and()
                .csrf().disable()
                .addFilterBefore(createClientFilter(), BasicAuthenticationFilter::class.java)
    }

    @Bean
    fun localAuthenticationFailureHandler() = LocalAuthenticationFailureHandler()

    @Bean
    fun predefinedUsers(): Map<String, UserPrincipal> = listOf(adminUser()).map {
        PredefinedUserPrincipal(
                id = it.username,
                userPassword =  passwordEncoder().encode(it.password),
                userAuthorities = listOf(SimpleGrantedAuthority(AuthorityName.ROLE_SUPER.name))
        )
    }.associateBy { it.username }

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    @ConfigurationProperties("superUser")
    fun adminUser() = PredefinedUserResource()

    // https://github.com/spring-guides/tut-spring-security-and-angular-js/blob/master/oauth2/authserver/src/main/java/demo/AuthserverApplication.java

    @Bean
    @ConfigurationProperties("github")
    fun github() = ClientResource(ExternalIdentityExtractor(
            idKey = "sub",
            identityType = IdentityType.GITHUB,
            mailKey = "email",
            firstNameKey = "name",
            lastNameKey = "surname",
            externalIdentityBinder = userIdentityBinder
    ))

    @Bean
    @ConfigurationProperties("facebook")
    fun facebook() = ClientResource(ExternalIdentityExtractor(
            idKey = "sub",
            identityType = IdentityType.FACEBOOK,
            mailKey = "email",
            firstNameKey = "name",
            lastNameKey = "surname",
            externalIdentityBinder = userIdentityBinder
    ))

    @Bean
    @ConfigurationProperties("google")
    fun google() = ClientResource(ExternalIdentityExtractor(
            idKey = "sub",
            identityType = IdentityType.GOOGLE,
            mailKey = "email",
            firstNameKey = "name",
            lastNameKey = "surname",
            externalIdentityBinder = userIdentityBinder
    ))

    private fun createClientFilter(): Filter {
        val filter = CompositeFilter()
        val filters = ArrayList<Filter>()
        filters.add(createClientFilter(facebook(), "/login/facebook"))
        filters.add(createClientFilter(google(), "/login/google"))
        filters.add(createClientFilter(github(), "/login/github"))
        filter.setFilters(filters)
        return filter
    }

    private fun createClientFilter(client: ClientResource, path: String): Filter {
        val filter = OAuth2ClientAuthenticationProcessingFilter(
                path)
        filter.setAuthenticationSuccessHandler(object: SavedRequestAwareAuthenticationSuccessHandler() {

            override fun onAuthenticationSuccess(request: HttpServletRequest?, response: HttpServletResponse?, authentication: Authentication?) {
                super.onAuthenticationSuccess(request, response, authentication)
            }

        })
        filter.setApplicationEventPublisher(eventPublisher);
        val template = OAuth2RestTemplate(client.client, oauth2ClientContext)
        filter.setRestTemplate(template)
        val tokenServices = UserInfoTokenServices(
                client.resource.userInfoUri, client.client.clientId)
        tokenServices.setRestTemplate(template)
        tokenServices.setPrincipalExtractor(client.identityExtractor)
        tokenServices.setAuthoritiesExtractor(client.identityExtractor)
        filter.setTokenServices(tokenServices)
        return filter
    }

    @Bean
    fun oauth2ClientFilterRegistration(filter: OAuth2ClientContextFilter): FilterRegistrationBean {
        val registration = FilterRegistrationBean()
        registration.filter = filter
        registration.order = -100
        return registration
    }

    /**
     * This class has to have public getters for property injection to work.
     */
    class ClientResource(

            val identityExtractor: ExternalIdentityExtractor,

            @NestedConfigurationProperty
            val client: AuthorizationCodeResourceDetails = AuthorizationCodeResourceDetails(),

            @NestedConfigurationProperty
            val resource: ResourceServerProperties = ResourceServerProperties()

    )

    /**
     * This class has to have public getters for property injection to work.
     */
    class PredefinedUserResource(

            var username: String = "",

            var password: String = ""

    )

    class UnauthorizedMultipleMethodsEntryPoint(val realmName: String) : AuthenticationEntryPoint {

        override fun commence(request: HttpServletRequest, response: HttpServletResponse, authException: AuthenticationException) {
            response.addHeader("WWW-Authenticate", "Basic realm=\"" + realmName + "\"")
            response.addHeader("WWW-Authenticate", "Bearer realm=\"" + realmName + "\"")
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    authException.message)
        }

    }

}
