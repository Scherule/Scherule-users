package com.scherule.users.controllers.resources

import com.scherule.users.services.UserPrincipal
import org.springframework.stereotype.Component

@Component
class ResourceFactory {

    fun toUserResource(principal: UserPrincipal) = UserResource(
            username = principal.username,
            firstName = principal.getFirstName(),
            lastName = principal.getLastName()
    )

}