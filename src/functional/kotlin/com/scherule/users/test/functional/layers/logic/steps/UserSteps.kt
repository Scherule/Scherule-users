package com.scherule.users.test.functional.layers.logic.steps

import com.scherule.users.test.functional.managers.UsersManager
import cucumber.api.java8.En
import java.util.*


internal class UserSteps(
        private val usersManager: UsersManager,
        private val stepContext: StepContext
) : En {

    init {

        Given("there is a user") {
            stepContext.user = Optional.of(usersManager.createDummyUser())
        }

    }

}