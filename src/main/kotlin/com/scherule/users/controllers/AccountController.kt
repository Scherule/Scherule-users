package com.scherule.users.controllers

import com.scherule.users.controllers.resources.AccountResourceAssembler
import com.scherule.users.domain.commands.PasswordChangeCommand
import com.scherule.users.domain.commands.PasswordResetCommand
import com.scherule.users.domain.commands.SetNewPasswordRequest
import com.scherule.users.domain.models.Account
import com.scherule.users.domain.models.User
import com.scherule.users.domain.services.UserService
import com.scherule.users.exceptions.UserNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.hateoas.EntityLinks
import org.springframework.hateoas.ExposesResourceFor
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.validation.ConstraintViolationException
import javax.validation.Valid

/**
 * Virtual representation of the user from his own perspective.
 * Not everything is exposed, some different things are present.
 */
@RestController
@RequestMapping("/api/account")
@ExposesResourceFor(Account::class)
class AccountController
@Autowired constructor(
        private val userService: UserService,
        private val accountResourceAssembler: AccountResourceAssembler,
        private val entityLinks: EntityLinks
) {

    @RequestMapping(method = arrayOf(RequestMethod.GET))
    fun getAccount() = accountResourceAssembler.toResource(Account.fromUser(userService.getActingUser())).apply {
        add(entityLinks.linkToSingleResource(User::class.java, id))
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = arrayOf(RequestMethod.PUT))
    fun putProfile(@RequestBody @Valid account: Account) {
        userService.updateProfile(account)
    }

    @RequestMapping(value = "/password", method = arrayOf(RequestMethod.POST))
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun changePassword(
            @RequestBody @Valid passwordChangeCommand: PasswordChangeCommand) {
        userService.changePasswordFor(passwordChangeCommand)
    }

    @RequestMapping(value = "/password/reset", method = arrayOf(RequestMethod.POST))
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun resetPassword(
            @RequestBody @Valid passwordResetCommand: PasswordResetCommand) {
        try {
            userService.resetPassword(passwordResetCommand)
        } catch (e: UserNotFoundException) {
            // we don't want to give hints about other emails
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/password/reset/confirmation", method = arrayOf(RequestMethod.POST))
    fun confirmPasswordReset(
            @RequestBody @Valid passwordResetRequest: SetNewPasswordRequest) {
        userService.confirmResetPassword(passwordResetRequest)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(e: ConstraintViolationException) {
        System.out.println("xyz");
    }

}
