package com.scherule.users.domain.repositories

import com.scherule.users.domain.models.User
import com.scherule.users.domain.models.UserCode
import com.scherule.users.domain.models.UserCodeType
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserCodesRepository : CrudRepository<UserCode, Long> {

    fun findByUserAndType(user: User, type: UserCodeType): Optional<UserCode>

    fun deleteByUser(user: User)

}