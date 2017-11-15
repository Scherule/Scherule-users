package com.scherule.users.domain.commands

import java.io.Serializable

data class PasswordResetCommand(
        var email: String
) : Serializable