package com.scherule.users.controllers.resources

import com.scherule.users.domain.models.User
import org.springframework.hateoas.ResourceSupport
import org.springframework.hateoas.core.Relation
import org.springframework.hateoas.mvc.ResourceAssemblerSupport
import org.springframework.stereotype.Component

@Relation(value="user", collectionRelation="users")
data class UserResource(
        val id: String,
        val username: String,
        val firstName: String?,
        val lastName: String?
) : ResourceSupport()

@Component
class UserResourceAssembler : ResourceAssemblerSupport<User, UserResource>(
        User::class.java,
        UserResource::class.java
) {

    override fun toResource(user: User) = UserResource(
            id = user.id!!,
            username = user.email,
            firstName = user.firstName,
            lastName = user.lastName
    )

}