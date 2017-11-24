package com.scherule.users.data

import com.scherule.users.domain.models.Account
import com.scherule.users.domain.models.UserModel


val RED_USER = UserModel(
        id = "red",
        firstName = "Agatha",
        email = "agatha@dummy.com",
        lastName = "Someone"
)

val RED_ACCOUNT = Account(
        id = "red",
        firstName = "Agatha",
        username = "agatha@dummy.com",
        lastName = "Someone"
)

val BLUE_USER = UserModel(
        id = "blue",
        firstName = "Greg",
        email = "greg@dummy.com",
        lastName = "Someone"
)