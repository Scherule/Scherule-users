package com.scherule.users.test.functional.layers.logic.steps

import com.scherule.users.domain.commands.AccountActivationCommand
import com.scherule.users.domain.models.UserCodeType
import com.scherule.users.domain.repositories.UserCodesRepository
import com.scherule.users.domain.repositories.UserRepository
import com.scherule.users.domain.services.UserService
import com.scherule.users.test.functional.aRegistrationCommand
import com.scherule.users.test.functional.layers.logic.AbstractSteps
import com.scherule.users.test.functional.layers.logic.StepContext
import com.scherule.users.test.functional.managers.UsersManager
import org.assertj.core.api.Assertions.assertThat
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.mail.MailSender
import org.springframework.mail.SimpleMailMessage

internal class RegistrationSteps
@Autowired constructor(
        private val usersManager: UsersManager,
        private val userService: UserService,
        private val userRepository: UserRepository,
        private val userCodesRepository: UserCodesRepository,
        private val stepContext: StepContext,
        @MockBean private val mailSender: MailSender
) : AbstractSteps() {

    var registrationCommandBuilder = aRegistrationCommand()

    init {

        Before { ->
            usersManager.resetAll()
        }

        Given("there is a registration request") {
            registrationCommandBuilder = aRegistrationCommand()
        }

        Given("this request is complete") {
            fillRegistrationRequest()
        }

        Given("the user has issued a valid registration request") {
            issueValidRegistrationRequest()
        }

        Given("the user has already activated his account") {
            issueValidRegistrationRequest()
            fetchUserCodeAndConfirm()
            refreshActingUser()
        }

        When("this request is issued") {
            issueRegistrationRequest()
        }

        When("he confirms his account using valid confirmation code") {
            fetchUserCodeAndConfirm()
        }

        When("he confirms his account using invalid confirmation code") {
            try { userService.activateUser(AccountActivationCommand("invalid")) } catch (e: Exception) {}
            refreshActingUser()
        }

        When("he uses the confirmation code already used for confirming the account") {
            confirmAccount()
        }

        Then("the user is created") {
            refreshActingUser()
            assertThat(stepContext.user).isNotNull()
        }

        Then("the user remains inactive") {
            assertThat(stepContext.user.map { it.enabled }.get()).isFalse()
        }

        Then("the user becomes active|the user is still active") {
            assertThat(stepContext.user.map { it.enabled }.get()).isTrue()
        }

        Then("he receives an error") {
            assertThat(stepContext.user).isNotNull()
        }

        Then("the user is sent an email with confirmation code") {
            val capturedMessage = ArgumentCaptor.forClass(SimpleMailMessage::class.java)
            Mockito.verify(mailSender).send(capturedMessage.capture())
            assertThat(capturedMessage.value).isNotNull()
            assertThat(capturedMessage.value.subject).isEqualTo("Your registration link at Expenses Tracker App")
            assertThat(capturedMessage.value.text).contains("If it is you who registered please follow the link")
        }

        Then("this action has no effect") {
            assertThat(stepContext.user.map { it.enabled }.get()).isTrue()
        }

    }

    private fun fetchUserCodeAndConfirm() {
        stepContext.userCode = userCodesRepository.findByUserAndType(stepContext.user.map { it.id }.get(), UserCodeType.REGISTRATION_CONFIRMATION)
        confirmAccount()
        refreshActingUser()
    }

    private fun confirmAccount() {
        try { userService.activateUser(AccountActivationCommand(stepContext.userCode.map { it.code }.get())) } catch(e: Exception) {}
    }

    private fun issueValidRegistrationRequest() {
        fillRegistrationRequest()
        issueRegistrationRequest()
        refreshActingUser()
    }

    private fun refreshActingUser() {
        stepContext.user = userRepository.findByEmail("someone@dummy.com")
    }

    private fun issueRegistrationRequest() {
        userService.registerUser(registrationCommandBuilder.build())
    }

    private fun fillRegistrationRequest() {
        registrationCommandBuilder.apply {
            username("someone@dummy.com")
            password("wHatE#ver3")
        }
    }

}