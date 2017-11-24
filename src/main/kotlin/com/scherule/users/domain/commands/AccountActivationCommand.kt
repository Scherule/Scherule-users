package com.scherule.users.domain.commands


data class AccountActivationCommand(
        val code: String
)