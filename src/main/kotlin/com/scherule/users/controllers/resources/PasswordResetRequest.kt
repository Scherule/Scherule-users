package com.scherule.users.controllers.resources

import java.io.Serializable

data class PasswordResetRequest(
        var email: String? = null
) : Serializable