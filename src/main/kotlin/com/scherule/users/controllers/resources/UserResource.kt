package com.scherule.users.controllers.resources

import com.scherule.users.domain.models.User
import com.scherule.users.domain.models.UserModel
import org.springframework.hateoas.Resource
import org.springframework.hateoas.core.Relation
import org.springframework.hateoas.mvc.ResourceAssemblerSupport
import org.springframework.stereotype.Component

@Relation(value="user", collectionRelation="users")
class UserResource(userModel: UserModel) : Resource<UserModel>(userModel)

@Component
class UserResourceAssembler : ResourceAssemblerSupport<UserModel, UserResource>(
        User::class.java,
        UserResource::class.java
) {

    override fun toResource(userModel: UserModel) = UserResource(userModel)

}