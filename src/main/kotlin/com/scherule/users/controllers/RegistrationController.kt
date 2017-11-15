package com.scherule.users.controllers

import com.scherule.users.controllers.resources.RegistrationConfirmationRequest
import com.scherule.users.controllers.resources.RegistrationRequest
import com.scherule.users.management.SystemRunner
import com.scherule.users.models.AuthorityName
import com.scherule.users.models.User
import com.scherule.users.repositories.AuthorityRepository
import com.scherule.users.services.UserService
import com.toptal.ggurgul.timezones.exceptions.RegistrationException
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping(value = "/api/registration")
class RegistrationController
@Autowired
constructor(
        private val userService: UserService
) {

    @RequestMapping(method = arrayOf(RequestMethod.POST))
    @ResponseStatus(HttpStatus.OK)
    @Throws(RegistrationException::class)
    fun registerUser(
            @RequestBody registrationRequest: RegistrationRequest
    ) = userService.registerUser(
            email = registrationRequest.email!!,
            password = registrationRequest.password!!
    )

    @RequestMapping(value = "/confirmation", method = arrayOf(RequestMethod.POST))
    @ResponseStatus(HttpStatus.OK)
    @Throws(RegistrationException::class)
    fun confirmUserRegistration(
            @RequestBody registrationConfirmationRequest: RegistrationConfirmationRequest
    ) = userService.activateUser(registrationConfirmationRequest.code)

}