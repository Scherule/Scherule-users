package com.scherule.users.domain.commands

import java.io.Serializable

data class SetNewPasswordRequest(
        var newPassword: String,
        var code: String
) : Serializable