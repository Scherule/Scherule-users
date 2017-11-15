package com.scherule.users.controllers

import com.scherule.users.domain.commands.UserActivationCommand
import com.scherule.users.domain.commands.RegistrationCommand
import com.scherule.users.controllers.resources.ResourceFactory
import com.scherule.users.domain.models.User
import com.scherule.users.domain.services.UserService
import com.toptal.ggurgul.timezones.exceptions.RegistrationException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.hateoas.EntityLinks
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping(value = "/api/registration")
class RegistrationController
@Autowired
constructor(
        private val userService: UserService,
        private val entityLinks: EntityLinks,
        private val resourceFactory: ResourceFactory
) {

    @RequestMapping(method = arrayOf(RequestMethod.POST))
    @ResponseStatus(HttpStatus.OK)
    @Throws(RegistrationException::class)
    fun registerUser(
            @RequestBody registrationCommand: RegistrationCommand
    ) = resourceFactory.toUserResource(userService.registerUser(registrationCommand)).apply {
        add(entityLinks.linkToSingleResource(User::class.java, id))
    }

    @RequestMapping(value = "/confirmation", method = arrayOf(RequestMethod.POST))
    @ResponseStatus(HttpStatus.OK)
    @Throws(RegistrationException::class)
    fun confirmUserRegistration(
            @RequestBody userActivationCommand: UserActivationCommand
    ) = userService.activateUser(userActivationCommand)

}