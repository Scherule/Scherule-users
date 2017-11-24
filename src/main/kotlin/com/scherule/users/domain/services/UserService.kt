package com.scherule.users.domain.services

import com.scherule.users.domain.models.*
import com.scherule.users.domain.repositories.AuthorityRepository
import com.scherule.users.domain.repositories.UserRepository
import com.scherule.users.management.SystemRunner
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class UserService(
        private val userContextService: UserContextService,
        private val userRepository: UserRepository,
        private val passwordEncoder: PasswordEncoder,
        private val authorityRepository: AuthorityRepository,
        private val userModelMapper: UserModelMapper
) {

    @PreAuthorize("hasRole('USER')")
    fun getActingUser() = userModelMapper.toUserModel(userContextService.getUser())

    fun getSome(page: Pageable) = userRepository.findAll(page).map(userModelMapper::toUserModel)

    /**
     * Creates user without confirmation constraint.
     * Used exclusively by super users and admins.
     */
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    @Transactional
    fun createUser(userModel: UserModel): UserModel {
        val user = userRepository.save(User(
                email = userModel.email,
                password = passwordEncoder.encode(userModel.password),
                authorities = mutableListOf(authorityRepository.findOne(AuthorityName.ROLE_USER)),
                enabled = true,
                firstName = userModel.firstName,
                lastName = userModel.lastName
        ))
        return userModelMapper.toUserModel(user)
    }

}