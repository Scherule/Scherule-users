package com.scherule.users.utils

import com.scherule.users.domain.services.UserPrincipalModel
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.test.context.support.WithSecurityContextFactory
import java.util.*
import kotlin.streams.toList

class ActingAsUserSecurityContextFactory : WithSecurityContextFactory<ActingAsUser> {

    override fun createSecurityContext(customUser: ActingAsUser): SecurityContext {
        val context = SecurityContextHolder.createEmptyContext()

        val principal = UserPrincipalModel(User(
                customUser.username, "", Arrays.stream(customUser.roles).map { SimpleGrantedAuthority(it) }.toList()
        ))

        val auth = UsernamePasswordAuthenticationToken(principal, "password", principal.authorities)
        context.authentication = auth
        return context
    }

}
