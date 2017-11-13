package com.scherule.users.exceptions


data class ApplicationErrorMessage(
        var code: Int? = null,
        var message: String? = null
)