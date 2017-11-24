package com.scherule.users.controllers

import com.scherule.users.data.RED_ACCOUNT
import com.scherule.users.data.RED_USER
import com.scherule.users.utils.ActingAsUser
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito
import org.mockito.Mockito
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

@WebMvcTest(AccountController::class)
class AccountControllerTest  : AbstractControllerTest() {

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
    @ActingAsUser
    @Throws(Exception::class)
    fun getAccount_200() {
        BDDMockito.given(accountService.getAccount()).willReturn(RED_ACCOUNT)
        mvc.perform(MockMvcRequestBuilders.get("/api/account")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().string("""{"username":"agatha@dummy.com","_links":{"self":{"href":"http://localhost/api/users/red"}}}"""))
    }

    private fun <T> anyObject(): T {
        return Mockito.any<T>()
    }

}