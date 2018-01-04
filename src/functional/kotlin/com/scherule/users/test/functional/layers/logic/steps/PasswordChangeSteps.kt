package com.scherule.users.test.functional.layers.logic.steps

import com.scherule.users.domain.commands.PasswordChangeCommand
import com.scherule.users.domain.services.AccountService
import com.scherule.users.domain.services.UserContextService
import com.scherule.users.test.functional.managers.UserContextSwitcher
import cucumber.api.java8.En
import java.util.*


internal class PasswordChangeSteps(
        private val accountService: AccountService,
        private val stepContext: StepContext,
        private val userContextSwitcher: UserContextSwitcher
) : En {

    init {

        When("the user changes his password") {
            stepContext.password = Optional.of("newSecret")
            userContextSwitcher.runAsUser(stepContext.user.get()) {
                accountService.changePassword(PasswordChangeCommand(
                        "secret",
                        stepContext.password.get()
                ))
            }
        }

    }

}