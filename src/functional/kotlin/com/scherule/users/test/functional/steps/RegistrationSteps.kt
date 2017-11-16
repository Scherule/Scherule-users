package com.scherule.users.test.functional.steps

import com.scherule.users.domain.repositories.UserRepository
import com.scherule.users.domain.services.UserService
import com.scherule.users.test.functional.AbstractSteps
import com.scherule.users.test.functional.StepContext
import com.scherule.users.test.functional.builders.aRegistrationCommand
import org.assertj.core.api.Assertions.assertThat
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.mail.MailSender
import org.springframework.mail.SimpleMailMessage

internal class RegistrationSteps
@Autowired constructor(
        private val userService: UserService,
        private val userRepository: UserRepository,
        private val stepContext: StepContext,
        @SpyBean private val mailSender: MailSender
) : AbstractSteps() {

    var registrationCommandBuilder = aRegistrationCommand()

    init {

        Given("there is a registration request") {
            registrationCommandBuilder = aRegistrationCommand()
        }

        Given("this request is complete") {
            registrationCommandBuilder.apply {
                username("someone@dummy.com")
                password("wHatE#ver3")
            }
        }

        When("this request is issued") {
            userService.registerUser(registrationCommandBuilder.build())
        }

        Then("the user is created") {
            stepContext.user = userRepository.findByEmail("someone@dummy.com")
            assertThat(stepContext.user).isNotNull()
        }

        Then("the user remains inactive") {
            assertThat(stepContext.user.get().enabled).isFalse()
        }

        Then("the user is sent an email with confirmation code") {
            val capturedMessage = ArgumentCaptor.forClass(SimpleMailMessage::class.java)
            Mockito.verify(mailSender).send(capturedMessage.capture())
            assertThat(capturedMessage.value).isNotNull()
            assertThat(capturedMessage.value.subject).isEqualTo("Your registration link at Expenses Tracker App")
            assertThat(capturedMessage.value.text).contains("If it is you who registered please follow the link")
        }

    }

}