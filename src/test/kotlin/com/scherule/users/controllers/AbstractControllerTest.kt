package com.scherule.users.controllers

import com.scherule.users.domain.repositories.UserRepository
import com.scherule.users.security.WebSecurityConfig
import com.scherule.users.domain.services.UserIdentityBinder
import com.scherule.users.domain.services.UserService
import org.junit.runner.RunWith
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner

/**
 * https://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#test-mockmvc-setup
 */
@RunWith(SpringRunner::class)
@ActiveProfiles("dev", "test")
@ContextConfiguration(classes = arrayOf(ControllerTestContext::class))
abstract class AbstractControllerTest {

    @MockBean
    protected lateinit var userDetailsService: UserDetailsService

    @MockBean
    protected lateinit var userRepository: UserRepository

    @MockBean
    protected lateinit var userIdentityBinder: UserIdentityBinder

    @MockBean
    protected lateinit var userService: UserService

}


@TestConfiguration
@Import(WebSecurityConfig::class)
@ComponentScan(basePackages = arrayOf("com.scherule.users.controllers", "com.scherule.users.utils"))
class ControllerTestContext {

//    @Bean
//    fun userDetailsService() = InMemoryUserDetailsService().apply {
//        addUser(UserPrincipalModel(User("alice@test.com", "", listOf(SimpleGrantedAuthority("ROLE_USER")))))
//    }

}