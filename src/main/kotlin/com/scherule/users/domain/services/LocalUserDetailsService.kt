package com.scherule.users.domain.services

import com.scherule.users.domain.models.IdentityType
import com.scherule.users.domain.models.UserPrincipalEntity
import com.scherule.users.domain.repositories.UserRepository
import com.scherule.users.exceptions.UserNotFoundException
import com.scherule.users.management.SystemRunner
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class LocalUserDetailsService
@Autowired constructor(
        private val userRepository: UserRepository,
        private val systemRunner: SystemRunner,
        private val predefinedUsersService: PredefinedUsersService
) : UserDetailsService {

    override fun loadUserByUsername(email: String) = predefinedUsersService.getPredefinedUser(email).orElseGet {
        systemRunner.runInSystemContext {
            userRepository.findByEmail(email).map { user -> UserPrincipalEntity(user) }
        }.orElseThrow { UserNotFoundException() }!!
    }

    fun findByIdentity(identity: String, identityType: IdentityType) = systemRunner.runInSystemContext {
        userRepository.findByIdentityAndType(identity, identityType)
    }

    fun findByEmail(mail: String) = systemRunner.runInSystemContext {
        userRepository.findByEmail(mail)
    }


}
