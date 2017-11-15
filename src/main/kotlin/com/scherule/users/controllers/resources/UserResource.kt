package com.scherule.users.controllers.resources

import org.springframework.hateoas.ResourceSupport


data class UserResource(
        val id: String,
        val username: String,
        val firstName: String?,
        val lastName: String?
) : ResourceSupport()