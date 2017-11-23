package com.scherule.users.controllers

import com.scherule.users.controllers.resources.UserResource
import com.scherule.users.controllers.resources.UserResourceAssembler
import com.scherule.users.domain.models.User
import com.scherule.users.domain.repositories.UserRepository
import com.scherule.users.domain.services.UserService
import com.scherule.users.exceptions.UserNotFoundException
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.ExposesResourceFor
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*
import javax.websocket.server.PathParam
import org.springframework.hateoas.mvc.ControllerLinkBuilder.*
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMethod
import javax.ws.rs.Consumes

/**
 *
 * https://stackoverflow.com/questions/16790371/spring-mvc-3-return-a-spring-data-page-as-json/16794740#16794740
 */
@Api("users", description = "Operations for user management.", tags = arrayOf("users"))
@RestController
@RequestMapping("/api/users")
@ExposesResourceFor(User::class)
class UserController
@Autowired constructor(
        private val userRepository: UserRepository,
        private val userService: UserService,
        private val userResourceAssembler: UserResourceAssembler,
        private val pagedUsersResourceAssembler: PagedResourcesAssembler<User>
) {

    /**
     * This endpoint gets called whenever the user authenticates on a page having no redirect back link.
     */
    @ApiOperation(value = "me", notes = "Get my user")
    @ApiResponses(ApiResponse(code = 200, message = "OK", response = UserResource::class))
    @GetMapping("/me")
    @Consumes(MediaType.ALL_VALUE)
    fun getUser() = userResourceAssembler.toResource(userService.getActingUser()).apply {
        add(linkTo(UserController::class.java).slash("/me").withSelfRel())
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping("/", method = arrayOf(RequestMethod.GET))
    fun getUserList(page: Pageable) = pagedUsersResourceAssembler.toResource(userRepository.findAll(page))

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping("/:id", method = arrayOf(RequestMethod.GET))
    fun getUserById(@PathParam("id") id: String) = userResourceAssembler.toResource(
            Optional.ofNullable(userRepository.findOne(id)).orElseThrow { UserNotFoundException() }
    )

}
