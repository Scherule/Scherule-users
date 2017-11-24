package com.scherule.users.domain.services

import com.scherule.users.domain.models.UserPrincipal
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import java.util.*

@Component
class PredefinedUsersService
@Autowired constructor(
        @Qualifier("predefinedUsers") private val predefinedUsers: Map<String, UserPrincipal>
) {

    fun getPredefinedUser(username: String) = Optional.ofNullable(predefinedUsers[username])

}