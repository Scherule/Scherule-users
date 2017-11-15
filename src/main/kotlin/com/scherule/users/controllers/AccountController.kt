package com.scherule.users.controllers

import com.scherule.users.domain.commands.PasswordChangeCommand
import com.scherule.users.domain.commands.PasswordResetCommand
import com.scherule.users.domain.commands.SetNewPasswordRequest
import com.scherule.users.domain.services.UserService
import com.scherule.users.exceptions.UserNotFoundException
import com.scherule.users.domain.models.UserAccount
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.ConstraintViolationException
import javax.validation.Valid

@RestController
@RequestMapping("/api/account")
class AccountController
@Autowired constructor(
        private val userService: UserService
) {

    @RequestMapping(method = arrayOf(RequestMethod.GET))
    fun getProfile(): ResponseEntity<UserAccount> {
        return ResponseEntity.ok(UserAccount.fromUser(userService.getActingUser()));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = arrayOf(RequestMethod.PUT))
    fun putProfile(@RequestBody @Valid userAccount: UserAccount) {
        userService.updateProfile(userAccount)
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
