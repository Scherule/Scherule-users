package com.scherule.users.controllers.resources

import com.scherule.users.domain.models.User
import com.scherule.users.domain.services.UserPrincipal
import org.springframework.stereotype.Component

@Component
class ResourceFactory {

    fun toUserResource(principal: UserPrincipal) = UserResource(
            id = principal.id()!!,
            username = principal.username,
            firstName = principal.getFirstName(),
            lastName = principal.getLastName()
    )

    fun toUserResource(user: User) = UserResource(
            id = user.id!!,
            username = user.email,
            firstName = user.firstName,
            lastName = user.lastName
    )

}