package com.scherule.users.controllers

import com.scherule.users.repositories.UserRepository
import com.scherule.users.services.UserService
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@WebMvcTest(UserController::class)
class UserControllerTest : AbstractControllerTest() {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    private lateinit var userRepository: UserRepository

    @MockBean
    private lateinit var userService: UserService

    @Test
    @WithUserDetails("alice@test.com")
    @Throws(Exception::class)
    fun canGetUser() {
        this.mvc.perform(get("/api/users/me").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk).andExpect(content().string("""{"username":"alice@test.com","firstName":null,"lastName":null,"_links":{"self":{"href":"http://localhost/api/users/alice@test.com"}}}"""))
    }

}
