package com.scherule.users.test.functional.managers

import com.scherule.users.domain.models.Authority
import com.scherule.users.domain.models.AuthorityName
import com.scherule.users.domain.models.User
import com.scherule.users.domain.repositories.AuthorityRepository
import com.scherule.users.domain.repositories.UserCodesRepository
import com.scherule.users.domain.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestComponent
import org.springframework.security.crypto.password.PasswordEncoder
import javax.transaction.Transactional

@TestComponent
class UsersManager {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var userCodesRepository: UserCodesRepository

    @Autowired
    private lateinit var authorityRepository: AuthorityRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Transactional
    fun createDummyUser() = userRepository.save(User(
            email = "someone@anyone.com",
            password = passwordEncoder.encode("secret"),
            enabled = true,
            authorities = mutableListOf(Authority(AuthorityName.ROLE_USER))
    ))!!

    fun resetAll() {
        userCodesRepository.deleteAll()
        userRepository.deleteAll()
        authorityRepository.deleteAll()
        authorityRepository.save(Authority(AuthorityName.ROLE_USER))
    }

}