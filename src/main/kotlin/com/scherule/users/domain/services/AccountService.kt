package com.scherule.users.domain.services

import com.scherule.users.domain.commands.*
import com.scherule.users.domain.models.Account
import com.scherule.users.domain.models.AuthorityName
import com.scherule.users.domain.models.User
import com.scherule.users.domain.models.UserCodeType
import com.scherule.users.domain.repositories.AuthorityRepository
import com.scherule.users.domain.repositories.UserRepository
import com.scherule.users.events.PasswordReset
import com.scherule.users.events.PasswordResetCodeIssued
import com.scherule.users.events.RegistrationCodeIssuedEvent
import com.scherule.users.exceptions.UserNotFoundException
import com.scherule.users.exceptions.WrongConfirmationCodeException
import com.scherule.users.management.SystemRunner
import com.toptal.ggurgul.timezones.exceptions.InvalidPasswordException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import javax.transaction.Transactional

@Component
class AccountService
@Autowired constructor(
        private val userContextService: UserContextService,
        private val userRepository: UserRepository,
        private val userCodesService: UserCodesService,
        private val eventPublisher: ApplicationEventPublisher,
        private val passwordEncoder: PasswordEncoder,
        private val systemRunner: SystemRunner,
        private val authorityRepository: AuthorityRepository
        ){

    @Transactional
    fun changePassword(passwordChangeCommand: PasswordChangeCommand) {
        val actingUser = userContextService.getUser()
        if (passwordEncoder.matches(passwordChangeCommand.oldPassword, actingUser.password)) {
            actingUser.password = passwordEncoder.encode(passwordChangeCommand.newPassword);
        } else {
            throw InvalidPasswordException();
        }
    }

   @Transactional
    fun activateAccount(accountActivationCommand: AccountActivationCommand) = systemRunner.runInSystemContext {
        userCodesService.consumeUserCode(accountActivationCommand.code, UserCodeType.REGISTRATION_CONFIRMATION) {
            userRepository.save(it.user!!.apply { enabled = true })
        }
    }

    @Transactional
    fun updateProfile(account: Account) {
        userContextService.getUser().apply {
            firstName = account.firstName
            lastName = account.lastName
        }
    }

    @Transactional
    fun registerUser(registrationCommand: RegistrationCommand): User = systemRunner.runInSystemContext {
        try {
            val user = userRepository.save(User(
                    email = registrationCommand.email,
                    password = passwordEncoder.encode(registrationCommand.password),
                    authorities = mutableListOf(authorityRepository.findOne(AuthorityName.ROLE_USER)),
                    enabled = false,
                    firstName = registrationCommand.firstName,
                    lastName = registrationCommand.lastName
            ))
            val userCode = userCodesService.issueUserCode(user, UserCodeType.REGISTRATION_CONFIRMATION)
            eventPublisher.publishEvent(RegistrationCodeIssuedEvent(userCode))
            user
        } catch (e: Exception) {
            throw DuplicateUserException(e)
        }
    }

    @Transactional
    @Throws(UserNotFoundException::class)
    fun resetPassword(passwordResetCommand: PasswordResetCommand) {
        val user = systemRunner.runInSystemContext {
            userRepository.findByEmail(passwordResetCommand.email)
        }.orElseThrow { UserNotFoundException() }
        val userCode = userCodesService.issueUserCode(user, UserCodeType.PASSWORD_RESET)
        eventPublisher.publishEvent(PasswordResetCodeIssued(userCode))
    }

    @Transactional
    @Throws(WrongConfirmationCodeException::class)
    fun confirmResetPassword(setNewPasswordRequest: SetNewPasswordRequest) = systemRunner.runInSystemContext {
        userCodesService.consumeUserCode(setNewPasswordRequest.code, UserCodeType.PASSWORD_RESET) {
            userRepository.save(it.user!!.apply { password = passwordEncoder.encode(setNewPasswordRequest.newPassword) })
            eventPublisher.publishEvent(PasswordReset(it.user!!))
        }
    }

    fun getAccount(): Account {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    class DuplicateUserException(e: Exception = IllegalStateException()) : RuntimeException(e)

}