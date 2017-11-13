package com.scherule.users.controllers.resources

import java.io.Serializable

data class SetNewPasswordRequest(
        var newPassword: String? = null,
        var code: String? = null
) : Serializable