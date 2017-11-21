package com.scherule.users.domain.models

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.security.Principal


interface UserPrincipal : UserDetails, Principal {

    fun id(): String

    fun getFirstName(): String?

    fun getLastName(): String?

}

class UserPrincipalEntity(
        @get:JsonIgnore
        val user: User
) : UserPrincipal {

    override fun id(): String {
        return user.id!!
    }

    override fun getFirstName() = user.firstName

    override fun getLastName() = user.lastName

    override fun getAuthorities() = user.authorities
            .map { authority -> SimpleGrantedAuthority(authority.name!!.name) }
            .toList()

    override fun getName() = username

    override fun isEnabled() = user.enabled

    override fun getUsername(): String = user.email

    @JsonIgnore
    override fun getPassword() = user.password

    override fun isCredentialsNonExpired() = true

    override fun isAccountNonExpired() = true

    override fun isAccountNonLocked() = true

}

data class PredefinedUserPrincipal(
        private val id: String,
        private val userPassword: String,
        private val userAuthorities: List<SimpleGrantedAuthority>
) : UserPrincipal {

    override fun id() = id

    override fun getFirstName() = null

    override fun getLastName() = null

    override fun getAuthorities() = userAuthorities

    override fun isEnabled() = true

    override fun getUsername() = id

    override fun isCredentialsNonExpired() = true

    override fun getPassword() = userPassword

    override fun isAccountNonExpired() = true

    override fun isAccountNonLocked() = true

    override fun getName() = id

}