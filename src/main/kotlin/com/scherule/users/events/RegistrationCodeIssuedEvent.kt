package com.scherule.users.events

import com.scherule.users.domain.models.UserCode
import org.springframework.context.ApplicationEvent

class RegistrationCodeIssuedEvent(userCodes: UserCode) : ApplicationEvent(userCodes) {

    override fun getSource() = super.getSource() as UserCode

}