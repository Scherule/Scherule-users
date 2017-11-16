package com.scherule.users.domain.services

import com.scherule.users.domain.commands.*
import com.scherule.users.domain.models.*
import com.scherule.users.domain.repositories.AuthorityRepository
import com.scherule.users.domain.repositories.UserCodesRepository
import com.scherule.users.domain.repositories.UserRepository
import com.scherule.users.exceptions.UserNotFoundException
import com.scherule.users.exceptions.WrongConfirmationCodeException
import com.scherule.users.management.SystemRunner
import com.toptal.ggurgul.timezones.domain.events.PasswordResetCodeIssued
import com.scherule.users.events.RegistrationCodeIssuedEvent
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
        private val userCodeTranslator: UserCodeTranslator,
        private val userCodesRepository: UserCodesRepository,
        private val userCodeFactory: UserCodeFactory,
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
    fun activateUser(userActivationCommand: UserActivationCommand) = systemRunner.runInSystemContext {
        val decodedCode = userCodeTranslator.readFrom(userActivationCommand.code)
        val userId: String = decodedCode.substringBefore(":")

        val user = Optional.of(userRepository.findOne(userId)).orElseThrow { IllegalStateException() }

        val userCode = userCodesRepository.findByUserAndType(user, UserCodeType.REGISTRATION_CONFIRMATION)
                .orElseThrow { WrongConfirmationCodeException() }

        if (userActivationCommand.code == userCode.code) {
            user.enabled = true
            userCodesRepository.delete(userCode)
        } else throw IllegalStateException()
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
                email = registrationCommand.email!!,
                password = passwordEncoder.encode(registrationCommand.email!!),
                authorities = mutableListOf(authorityRepository.findOne(AuthorityName.ROLE_USER)),
                enabled = false,
                firstName = registrationCommand.firstName,
                lastName = registrationCommand.lastName
        ))

        val userCode = userCodeFactory.generateFor(user, UserCodeType.REGISTRATION_CONFIRMATION)

        userCodesRepository.save(userCode)
        eventPublisher.publishEvent(RegistrationCodeIssuedEvent(userCode))
        user
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
        val userCode = userCodeFactory.generateFor(user, UserCodeType.PASSWORD_RESET)
        userCodesRepository.save(userCode)
        eventPublisher.publishEvent(PasswordResetCodeIssued(userCode))
    }

    @Transactional
    @Throws(WrongConfirmationCodeException::class)
    fun confirmResetPassword(setNewPasswordRequest: SetNewPasswordRequest) = systemRunner.runInSystemContext {
        val decodedCode = userCodeTranslator.readFrom(setNewPasswordRequest.code)
        val username: String = decodedCode.substringBefore(":")

        val user = Optional.of(userRepository.findOne(username)).orElseThrow { WrongConfirmationCodeException() }

        val userCode = userCodesRepository.findByUserAndType(user, UserCodeType.PASSWORD_RESET)
                .orElseThrow { WrongConfirmationCodeException() }

        if (setNewPasswordRequest.code == userCode.code) {
            user.password = passwordEncoder.encode(setNewPasswordRequest.newPassword)
            userCodesRepository.delete(userCode)
        } else throw WrongConfirmationCodeException()
    }

}