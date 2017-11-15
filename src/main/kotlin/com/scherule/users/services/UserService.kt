package com.scherule.users.services

import com.scherule.users.management.SystemRunner
import com.scherule.users.models.User
import com.scherule.users.models.UserCodeType
import com.scherule.users.repositories.UserCodesRepository
import com.scherule.users.repositories.UserRepository
import com.toptal.ggurgul.timezones.domain.events.PasswordResetCodeIssued
import com.toptal.ggurgul.timezones.domain.events.RegistrationCodeIssuedEvent
import com.toptal.ggurgul.timezones.exceptions.InvalidPasswordException
import com.scherule.users.exceptions.UserNotFoundException
import com.scherule.users.exceptions.WrongConfirmationCodeException
import com.scherule.users.models.UserAccount
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
        private val systemRunner: SystemRunner
) {

    fun getActingUser(): User {
        val auth = SecurityContextHolder.getContext().authentication
        return Optional.ofNullable(userRepository.findOne((auth.principal as UserPrincipal).id())).orElseThrow {
            IllegalStateException("No user found")
        }
    }

    @Transactional
    fun activateUser(code: String) {
        val decodedCode = userCodeTranslator.readFrom(code)
        val userId: String = decodedCode.substringBefore(":")

        val user = Optional.of(userRepository.findOne(userId)).orElseThrow { IllegalStateException() }

        val userCode = userCodesRepository.findByUserAndType(user, UserCodeType.REGISTRATION_CONFIRMATION)
                .orElseThrow { WrongConfirmationCodeException() }

        return if (code == userCode.code) {
            user.enabled = true
            userCodesRepository.delete(userCode)
        } else throw IllegalStateException()
    }

    @Transactional
    fun changePasswordFor(user: User, oldPassword: String, newPassword: String) {
        if (passwordEncoder.matches(oldPassword, user.password)) {
            user.password = passwordEncoder.encode(newPassword);
        } else {
            throw InvalidPasswordException();
        }
    }

    @Transactional
    fun registerUser(user: User): User {
        user.enabled = false
        userRepository.save(user)
        val userCode = userCodeFactory.generateFor(user, UserCodeType.REGISTRATION_CONFIRMATION)
        userCodesRepository.save(userCode)
        eventPublisher.publishEvent(RegistrationCodeIssuedEvent(userCode))
        return user
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
    fun resetPassword(email: String) {
        val user = systemRunner.runInSystemContext {
            userRepository.findByEmail(email)
        }.orElseThrow { UserNotFoundException() }
        val userCode = userCodeFactory.generateFor(user, UserCodeType.PASSWORD_RESET)
        userCodesRepository.save(userCode)
        eventPublisher.publishEvent(PasswordResetCodeIssued(userCode))
    }

    @Transactional
    @Throws(WrongConfirmationCodeException::class)
    fun confirmResetPassword(code: String, newPassword: String) {
        val decodedCode = userCodeTranslator.readFrom(code)
        val username: String = decodedCode.substringBefore(":")

        val user = Optional.of(userRepository.findOne(username)).orElseThrow { WrongConfirmationCodeException() }

        val userCode = userCodesRepository.findByUserAndType(user, UserCodeType.PASSWORD_RESET)
                .orElseThrow { WrongConfirmationCodeException() }

        return if (code == userCode.code) {
            user.password = passwordEncoder.encode(newPassword)
            userCodesRepository.delete(userCode)
        } else throw WrongConfirmationCodeException()
    }

}