package com.scherule.users.controllers

import com.scherule.users.services.UserPrincipalModel
import com.scherule.users.utils.InMemoryUserDetailsService
import org.junit.runner.RunWith
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@ActiveProfiles("dev", "test")
@ContextConfiguration(classes = arrayOf(ControllerTestContext::class))
abstract class AbstractControllerTest


@TestConfiguration
@ComponentScan(basePackages = arrayOf("com.scherule.users.controllers", "com.scherule.users.utils"))
class ControllerTestContext {

    @Bean
    fun userDetailsService() = InMemoryUserDetailsService().apply {
        addUser(UserPrincipalModel(User("alice@test.com", "", listOf(SimpleGrantedAuthority("ROLE_USER")))))
    }

}