package com.scherule.users.utils

import org.springframework.security.test.context.support.WithSecurityContext
import kotlin.annotation.AnnotationRetention.RUNTIME

@Retention(RUNTIME)
@WithSecurityContext(factory = ActingAsUserSecurityContextFactory::class)
annotation class ActingAsUser(
        val username: String = "someone@test.com",
        val roles: Array<String> = arrayOf("ROLE_USER")
)