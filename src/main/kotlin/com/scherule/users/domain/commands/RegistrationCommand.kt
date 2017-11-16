package com.scherule.users.domain.commands

import java.io.Serializable

data class RegistrationCommand(
        val email: String,
        val password: String,
        val firstName: String? = null,
        val lastName: String? = null
) : Serializable