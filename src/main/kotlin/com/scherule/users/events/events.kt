package com.scherule.users.events

import com.scherule.users.domain.models.User
import com.scherule.users.domain.models.UserCode
import org.springframework.context.ApplicationEvent


class PasswordReset(user: User) : ApplicationEvent(user) {

    override fun getSource() = super.getSource() as User

}

class PasswordResetCodeIssued(userCode: UserCode) : ApplicationEvent(userCode) {

    override fun getSource() = super.getSource() as UserCode

}

class RegistrationCodeIssuedEvent(userCodes: UserCode) : ApplicationEvent(userCodes) {

    override fun getSource() = super.getSource() as UserCode

}