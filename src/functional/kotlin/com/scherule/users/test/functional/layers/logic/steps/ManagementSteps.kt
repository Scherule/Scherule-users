package com.scherule.users.test.functional.layers.logic.steps

import com.scherule.users.domain.models.User
import com.scherule.users.domain.repositories.UserRepository
import com.scherule.users.domain.services.UserService
import com.scherule.users.test.functional.layers.logic.AbstractSteps
import org.assertj.core.api.Assertions.assertThat
import org.springframework.beans.factory.annotation.Autowired

internal class ManagementSteps
@Autowired constructor(
        private val userService: UserService,
        private val userRepository: UserRepository
) : AbstractSteps() {

    init {

        When("super user creates admin user") {
            userService.createUser(User(
                    email = "abc@test.com",
                    password = "abcdef",
                    enabled = true
            ))
        }

        When("super user does any other action") {

        }

        Then("the admin user is created") {
            assertThat(userRepository.findByEmail("abc@test.com")).isNotNull()
        }

        Then("he is forbidden to do so") {

        }

    }

}