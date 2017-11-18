package com.scherule.users.controllers.exceptions

import com.scherule.users.exceptions.UserNotFoundException
import com.scherule.users.domain.services.UserCodesService
import com.scherule.users.domain.services.UserService
import org.apache.commons.logging.LogFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus


@ControllerAdvice
class ExceptionAdvice {

    companion object {
        private val LOG = LogFactory.getLog(ExceptionAdvice::class.java)
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)  // 409
    @ExceptionHandler(UserNotFoundException::class)
    fun handleUnauthorized(): ErrorResponse{
        LOG.debug("Unauthorized login attempt.")
        return ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Failed login attempt.")
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST, reason = "The code used is malformed or no longer is active.")
    @ExceptionHandler(UserCodesService.MalformedUserCodeException::class)
    fun malformedUserCode() {

    }

    @ResponseStatus(HttpStatus.BAD_REQUEST, reason = "This e-mail address is restricted or is already used. Try to log in.")
    @ExceptionHandler(UserService.DuplicateUserException::class)
    fun duplicateUser() {

    }

}

data class ErrorResponse(
        val code: Int,
        val message: String
)