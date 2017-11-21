package com.scherule.users.controllers

import com.scherule.users.domain.commands.RegistrationCommand
import com.scherule.users.domain.commands.UserActivationCommand
import com.scherule.users.domain.services.UserService
import com.toptal.ggurgul.timezones.exceptions.RegistrationException
import io.swagger.annotations.Api
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.hateoas.EntityLinks
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@Api("registration", description = "Operations to perform registration actions.", tags = arrayOf("registration"))
@RestController
@RequestMapping(value = "/api/registration")
class RegistrationController
@Autowired
constructor(
        private val userService: UserService
) {

    @RequestMapping(method = arrayOf(RequestMethod.POST))
    @ResponseStatus(HttpStatus.CREATED)
    @Throws(RegistrationException::class)
    fun registerUser(
            @RequestBody registrationCommand: RegistrationCommand
    ) {
        userService.registerUser(registrationCommand)
    }

    @RequestMapping(value = "/confirmation", method = arrayOf(RequestMethod.POST))
    @ResponseStatus(HttpStatus.OK)
    @Throws(RegistrationException::class)
    fun confirmUserRegistration(
            @RequestBody userActivationCommand: UserActivationCommand
    ) = userService.activateUser(userActivationCommand)

}