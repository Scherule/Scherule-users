package com.scherule.users.domain.services

import com.scherule.users.domain.commands.*
import com.scherule.users.domain.models.*
import com.scherule.users.domain.repositories.AuthorityRepository
import com.scherule.users.domain.repositories.UserCodesRepository
import com.scherule.users.domain.repositories.UserRepository
import com.scherule.users.events.PasswordReset
import com.scherule.users.events.PasswordResetCodeIssued
import com.scherule.users.events.RegistrationCodeIssuedEvent
import com.scherule.users.exceptions.UserNotFoundException
import com.scherule.users.exceptions.WrongConfirmationCodeException
import com.scherule.users.management.SystemRunner
import com.toptal.ggurgul.timezones.exceptions.InvalidPasswordException
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*
import javax.transaction.Transactional

@Service
class UserService(
        private val userRepository: UserRepository,
        private val userCodesService: UserCodesService,
        private val userCodesRepository: UserCodesRepository,
        private val eventPublisher: ApplicationEventPublisher,
        private val passwordEncoder: PasswordEncoder,
        private val systemRunner: SystemRunner,
        private val authorityRepository: AuthorityRepository
) {

    fun getActingUser(): User {
        val auth = SecurityContextHolder.getContext().authentication
        return Optional.ofNullable(userRepository.findOne((auth.principal as UserPrincipal).id())).orElseThrow {
            IllegalStateException("No user found")
        }
    }

    @Transactional
    fun changePasswordFor(passwordChangeCommand: PasswordChangeCommand) {
        val actingUser = getActingUser()
        if (passwordEncoder.matches(passwordChangeCommand.oldPassword, actingUser.password)) {
            actingUser.password = passwordEncoder.encode(passwordChangeCommand.newPassword);
        } else {
            throw InvalidPasswordException();
        }
    }

    @Transactional
    fun registerUser(registrationCommand: RegistrationCommand): User = systemRunner.runInSystemContext {
        val user = userRepository.save(User(
                email = registrationCommand.email,
                password = passwordEncoder.encode(registrationCommand.email),
                authorities = mutableListOf(authorityRepository.findOne(AuthorityName.ROLE_USER)),
                enabled = false,
                firstName = registrationCommand.firstName,
                lastName = registrationCommand.lastName
        ))
        val userCode = userCodesService.issueUserCode(user, UserCodeType.REGISTRATION_CONFIRMATION)
        eventPublisher.publishEvent(RegistrationCodeIssuedEvent(userCode))
        user
    }

    @Transactional
    fun activateUser(userActivationCommand: UserActivationCommand) = systemRunner.runInSystemContext {
        userCodesService.consumeUserCode(userActivationCommand.code, UserCodeType.REGISTRATION_CONFIRMATION) {
            userRepository.save(it.user!!.apply { enabled = true })
        }
    }

    @Transactional
    fun updateProfile(userAccount: UserAccount) {
        getActingUser().apply {
            firstName = userAccount.firstName
            lastName = userAccount.lastName
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

}