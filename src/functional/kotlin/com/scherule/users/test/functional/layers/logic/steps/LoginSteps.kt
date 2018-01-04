package com.scherule.users.test.functional.layers.logic.steps

import cucumber.api.java8.En
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.assertj.core.api.Assertions.*


internal class LoginSteps(
        private val stepContext: StepContext,
        private val authenticationManager: AuthenticationManager
) : En {

    init {

        Then("he can log in using new password") {
            assertThat(doesAuthenticate(stepContext.password.get())).isTrue()
        }

        Then("he cannot log in using old password") {
            assertThat(doesAuthenticate("secret")).isFalse()
        }

    }

    private fun doesAuthenticate(password: String): Boolean {
        try {
            return authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken(
                            stepContext.user.map { it.email }.get(),
                            password
                    )
            )!!.isAuthenticated
        } catch (e: Exception) {
            return false
        }
    }

}