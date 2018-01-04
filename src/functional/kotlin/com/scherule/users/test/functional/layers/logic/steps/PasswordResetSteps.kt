package com.scherule.users.test.functional.layers.logic.steps

import com.scherule.users.domain.commands.PasswordResetCommand
import com.scherule.users.domain.commands.SetNewPasswordRequest
import com.scherule.users.domain.models.UserCodeType
import com.scherule.users.domain.repositories.UserCodesRepository
import com.scherule.users.domain.services.AccountService
import com.scherule.users.test.functional.managers.UsersManager
import cucumber.api.java8.En
import org.assertj.core.api.Assertions
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.MailSender
import org.springframework.mail.SimpleMailMessage
import java.util.*


internal class PasswordResetSteps
@Autowired constructor(
        private val stepContext: StepContext,
        private val accountService: AccountService,
        private val usersManager: UsersManager,
        private val mailSender: MailSender,
        private val userCodesRepository: UserCodesRepository
) : En {

    init {

        Given("the user has requested to reset the password") {
            issueResetPasswordRequest()
        }

        When("the user resets password") {
            issueResetPasswordRequest()
        }

        When("the user sets the new password using correct code") {
            stepContext.password = Optional.of("abcdef")
            accountService.confirmResetPassword(SetNewPasswordRequest(stepContext.password.get(), getIssuedPasswordResetCode()))
        }

        Then("the password reset email is sent to him") {
            val passwordResetCode = getIssuedPasswordResetCode()

            val capturedMessage = ArgumentCaptor.forClass(SimpleMailMessage::class.java)
            Mockito.verify(mailSender).send(capturedMessage.capture())
            Assertions.assertThat(capturedMessage.value).isNotNull()
            Assertions.assertThat(capturedMessage.value.subject).isEqualTo("Reset your password at Expenses Tracker App")
            Assertions.assertThat(capturedMessage.value.text).contains("Did you request password reset?\n" +
                    "If you did please follow the link http://localhost:3000/#/reset-password?resetcode=$passwordResetCode to end the process!\n" +
                    "If not, please disregard this message.")
        }

    }

    private fun getIssuedPasswordResetCode(): String {
        return userCodesRepository.findByUserAndType(
                stepContext.user.map { it.id }.get(),
                UserCodeType.PASSWORD_RESET
        ).get().code!!
    }

    private fun issueResetPasswordRequest() {
        accountService.resetPassword(PasswordResetCommand(stepContext.user.map { it.email }.get()))
    }

}