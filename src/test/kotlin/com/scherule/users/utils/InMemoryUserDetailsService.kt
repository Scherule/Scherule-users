package com.scherule.users.utils

import com.scherule.users.domain.models.UserPrincipal
import org.springframework.security.core.userdetails.UserDetailsService

class InMemoryUserDetailsService(
        private val users: MutableMap<String, UserPrincipal> = mutableMapOf()
) : UserDetailsService {

    fun addUser(user: UserPrincipal) = users.put(user.name, user)

    override fun loadUserByUsername(username: String?) = users[username]

}