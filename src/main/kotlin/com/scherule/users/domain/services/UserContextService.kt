package com.scherule.users.domain.services

import com.scherule.users.domain.models.UserPrincipal
import com.scherule.users.domain.repositories.UserRepository
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.provider.OAuth2Authentication


class UserContextService(
        private val userRepository: UserRepository
        ) {

    internal fun getUser() = userRepository.findOne(getUserPrincipal().id())

    private fun getUserPrincipal(): UserPrincipal {
        val auth = SecurityContextHolder.getContext().authentication
        return when (auth) {
            is OAuth2Authentication -> auth.userAuthentication.principal
            is UsernamePasswordAuthenticationToken -> auth.principal
            else -> auth
        } as UserPrincipal
    }

}