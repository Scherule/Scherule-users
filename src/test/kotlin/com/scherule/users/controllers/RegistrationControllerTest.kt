package com.scherule.users.controllers

import com.scherule.users.domain.commands.RegistrationCommand
import com.scherule.users.domain.commands.UserActivationCommand
import com.scherule.users.domain.models.User
import com.scherule.users.domain.services.UserCodesService
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.contains
import org.mockito.BDDMockito.given
import org.mockito.Matchers
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@WebMvcTest(RegistrationController::class)
class RegistrationControllerTest : AbstractControllerTest() {

    @Autowired
    private lateinit var context: WebApplicationContext

    @Autowired
    private lateinit var mvc: MockMvc

    @Before
    fun setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .`apply`<DefaultMockMvcBuilder>(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @Throws(Exception::class)
    fun postRegister_valid_200() {
        given(userService.registerUser(RegistrationCommand(
                email = "hello.kitty@dummy.com",
                password = "peterPan123",
                firstName = "Alice",
                lastName = "Someone"
        ))).willReturn(User(
                id = "abecadlo",
                firstName = "Alice",
                email = "hello.kitty@dummy.com",
                lastName = "Someone"
        ))
        mvc.perform(MockMvcRequestBuilders.post("/api/registration")
                .content("""
                    {
                        "email": "hello.kitty@dummy.com",
                        "password": "peterPan123",
                        "firstName": "Alice",
                        "lastName": "Someone"
                    }
                """.trim())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().string("""{"username":"hello.kitty@dummy.com","firstName":"Alice","lastName":"Someone","_links":{"self":{"href":"http://localhost/api/users/abecadlo"}}}"""))
    }

    @Test
    @Throws(Exception::class)
    fun postRegistrationConfirmation_valid_200() {
        mvc.perform(MockMvcRequestBuilders.post("/api/registration/confirmation")
                .content("""
                    {
                        "code": "abcdef"
                    }
                """.trim())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    @Throws(Exception::class)
    fun postRegistrationConfirmation_invalidCode_400() {
        given(userService.activateUser(UserActivationCommand("abcdef"))).willThrow(UserCodesService.MalformedUserCodeException())
        mvc.perform(MockMvcRequestBuilders.post("/api/registration/confirmation")
                .content("""
                    {
                        "code": "abcdef"
                    }
                """.trim())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError)
                .andExpect(MockMvcResultMatchers.status().reason("The code used is malformed or no longer is active."))
    }

}