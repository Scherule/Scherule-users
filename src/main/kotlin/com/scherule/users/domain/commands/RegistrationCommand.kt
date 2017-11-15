package com.scherule.users.domain.commands

import java.io.Serializable

data class RegistrationCommand(
        var email: String? = null,
        var password: String? = null,
        val firstName: String? = null,
        val lastName: String? = null
) : Serializable