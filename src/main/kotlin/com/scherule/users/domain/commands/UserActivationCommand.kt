package com.scherule.users.domain.commands


data class UserActivationCommand(
        val code: String
)