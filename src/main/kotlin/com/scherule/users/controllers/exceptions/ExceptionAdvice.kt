package com.scherule.users.controllers.exceptions

import com.scherule.users.exceptions.UserNotFoundException
import org.apache.commons.logging.LogFactory
import org.springframework.http.HttpStatus
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
    fun handleUnauthorized(){
        LOG.debug("Unauthorized login attempt.")
    }

}