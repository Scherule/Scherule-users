package com.scherule.users.controllers

import com.scherule.users.models.User
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
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
    fun canPostRegister() {
        given(userService.registerUser("hello.kitty@dummy.com", "peterPan123")).willReturn(User(
                email = "hello.kitty@dummy.com"
        ))
        mvc.perform(MockMvcRequestBuilders.post("/api/registration")
                .content("""
                    {
                        "email": "hello.kitty@dummy.com",
                        "password": "peterPan123"
                    }
                """.trim())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().string("""{"username":"alice@test.com","firstName":null,"lastName":null,"_links":{"self":{"href":"http://localhost/api/users/alice@test.com"}}}"""))
    }

}