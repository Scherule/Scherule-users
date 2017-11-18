package com.scherule.users.domain.repositories

import com.scherule.users.domain.models.User
import com.scherule.users.domain.models.UserCode
import com.scherule.users.domain.models.UserCodeType
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserCodesRepository : CrudRepository<UserCode, Long> {

    @Query("select w from UserCode w where w.user.id = :userId and w.type = :type")
    fun findByUserAndType(@Param("userId") userId: String, @Param("type") type: UserCodeType): Optional<UserCode>

    fun deleteByUser(user: User)

}