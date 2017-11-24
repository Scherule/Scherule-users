package com.scherule.users.test.functional.layers.logic.steps

import com.scherule.users.domain.models.UserModel
import com.scherule.users.domain.repositories.UserRepository
import com.scherule.users.domain.services.UserService
import com.scherule.users.test.functional.managers.UserContextSwitcher
import com.scherule.users.test.functional.managers.UsersManager
import cucumber.api.java8.En
import org.assertj.core.api.Assertions.assertThat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

internal class ManagementSteps
@Autowired constructor(
        private val userService: UserService,
        private val userRepository: UserRepository,
        private val userContextSwitcher: UserContextSwitcher
) : En {

    init {

        When("super user creates admin user") {
            userContextSwitcher.runAsSuperUser {
                userService.createUser(UserModel(
                        email = "abc@test.com",
                        password = "abcdef"
                ))
            }
        }

        When("super user does any other action") {
            assertThat(userRepository.findByEmail("abc@test.com")).isNotNull()
        }

        Then("the admin user is created") {
            assertThat(userRepository.findByEmail("abc@test.com")).isNotNull()
        }

        Then("he is forbidden to do so") {
            assertThat(userRepository.findByEmail("abc@test.com")).isNotNull()
        }

    }

}