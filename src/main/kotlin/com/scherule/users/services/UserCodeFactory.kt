package com.scherule.users.services

import com.scherule.users.models.User
import com.scherule.users.models.UserCode
import com.scherule.users.models.UserCodeType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Component
class UserCodeFactory
@Autowired constructor(private val codeTranslator: UserCodeTranslator) {

    fun generateFor(user: User, codeType: UserCodeType): UserCode {
        val userCode = UserCode()
        userCode.user = user
        userCode.type = codeType
        userCode.sentToEmail = user.email
        userCode.code = codeTranslator.generateFor(user.id!!)
        return userCode
    }


}