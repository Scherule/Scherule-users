package com.scherule.users.test.functional

import com.scherule.users.domain.commands.RegistrationCommand


fun aRegistrationCommand() = RegistrationCommandBuilder()


class RegistrationCommandBuilder {

    var username: String? = null
        private set

    var password: String? = null
        private set

    fun username(username: String) = apply { this.username = username }

    fun password(password: String) = apply { this.password = password }

    fun build() = RegistrationCommand(username!!, password!!)

}