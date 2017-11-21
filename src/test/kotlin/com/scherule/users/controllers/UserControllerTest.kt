package com.scherule.users.controllers

import com.scherule.users.data.BLUE_USER
import com.scherule.users.data.RED_USER
import com.scherule.users.utils.ActingAsUser
import org.junit.Test
import org.mockito.BDDMockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@WebMvcTest(UserController::class)
class UserControllerTest : AbstractControllerTest() {

    @Autowired
    private lateinit var mvc: MockMvc

    @Test
    @ActingAsUser
    @Throws(Exception::class)
    fun canGetUser() {
        BDDMockito.given(userService.getActingUser()).willReturn(RED_USER)
        this.mvc.perform(get("/api/users/me").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk).andExpect(content().string("""{"username":"agatha@dummy.com","firstName":"Agatha","lastName":"Someone","_links":{"self":{"href":"http://localhost/api/users/me"}}}"""))
    }

    @Test
    @ActingAsUser
    @Throws(Exception::class)
    fun getUsers_200() {
        val pageRequest = PageRequest(6, 2)
        BDDMockito.given(userRepository.findAll(pageRequest)).willReturn(PageImpl(listOf(RED_USER, BLUE_USER), pageRequest, 111))
        this.mvc.perform(get("/api/users/?size=2&page=6").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk).andExpect(content().string("""{"_embedded":{"userList":[{"id":"red","email":"agatha@dummy.com","firstName":"Agatha","lastName":"Someone","enabled":false,"authorities":[],"boundIdentities":[]},{"id":"blue","email":"greg@dummy.com","firstName":"Greg","lastName":"Someone","enabled":false,"authorities":[],"boundIdentities":[]}]},"_links":{"first":{"href":"http://localhost/api/users/?page=0&size=2"},"prev":{"href":"http://localhost/api/users/?page=5&size=2"},"self":{"href":"http://localhost/api/users/?page=6&size=2"},"next":{"href":"http://localhost/api/users/?page=7&size=2"},"last":{"href":"http://localhost/api/users/?page=55&size=2"}},"page":{"size":2,"totalElements":111,"totalPages":56,"number":6}}"""))
    }

}
