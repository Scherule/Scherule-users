package com.scherule.users.domain.commands

import org.hibernate.validator.constraints.Length
import java.io.Serializable
import javax.validation.constraints.NotNull

data class PasswordChangeCommand(
        @get:NotNull
        @get:Length(min = 6, max = 30)
        var oldPassword: String? = null,
        @get:NotNull
        @get:Length(min = 6, max = 30)
        var newPassword: String? = null
) : Serializable