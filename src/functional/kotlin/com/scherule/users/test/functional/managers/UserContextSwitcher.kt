package com.scherule.users.test.functional.managers

import com.scherule.users.domain.models.User
import com.scherule.users.domain.models.UserPrincipalEntity
import com.scherule.users.domain.services.PredefinedUsersService
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestComponent
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder

@TestComponent
class UserContextSwitcher(
        private val predefinedUsersService: PredefinedUsersService,
        @Value("\${superUser.username}") private val superUserName: String
) {

    fun runAsSuperUser(fn: () -> Unit) {
        val authenticationContext = SecurityContextHolder.getContext()
        val oldAuthentication = authenticationContext.authentication
        val superUser = predefinedUsersService.getPredefinedUser(superUserName).get()
        authenticationContext.authentication = UsernamePasswordAuthenticationToken(superUser, null, superUser.authorities);
        fn()
        authenticationContext.authentication = oldAuthentication
    }

    fun runAsUser(user: User, fn: () -> Unit) {
        val authenticationContext = SecurityContextHolder.getContext()
        val oldAuthentication = authenticationContext.authentication
        val userPrincipalEntity = UserPrincipalEntity(user)
        authenticationContext.authentication = UsernamePasswordAuthenticationToken(userPrincipalEntity, null, userPrincipalEntity.authorities);
        fn()
        authenticationContext.authentication = oldAuthentication
    }

}