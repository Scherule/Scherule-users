package com.scherule.users.controllers

import com.scherule.users.controllers.resources.ResourceFactory
import com.scherule.users.exceptions.UserNotFoundException
import com.scherule.users.models.User
import com.scherule.users.repositories.UserRepository
import com.scherule.users.services.UserPrincipal
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.hateoas.EntityLinks
import org.springframework.hateoas.ExposesResourceFor
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal
import java.util.*
import javax.websocket.server.PathParam


@RestController
@RequestMapping("/api/users")
@ExposesResourceFor(User::class)
class UserController {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var resourceFactory: ResourceFactory

    @Autowired
    private lateinit var entityLinks: EntityLinks

    /**
     * This endpoint gets called whenever the user authenticates on a page having no redirect back link.
     */
    @RequestMapping(path = arrayOf("/me"), produces = arrayOf(MediaType.ALL_VALUE))
    fun user(principal: Principal) = resourceFactory.toUserResource(when (principal) {
        is OAuth2Authentication -> principal.userAuthentication.principal
        is UsernamePasswordAuthenticationToken -> principal.principal
        else -> principal
    } as UserPrincipal).apply {
        add(entityLinks.linkToSingleResource(User::class.java, username))
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping("/:email")
    fun getById(@PathParam("id") id: String): User {
        return Optional.ofNullable(userRepository.findOne(id)).orElseThrow { UserNotFoundException() }
    }

}
