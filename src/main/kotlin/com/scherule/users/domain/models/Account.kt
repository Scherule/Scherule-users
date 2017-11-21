package com.scherule.users.domain.models

import org.hibernate.validator.constraints.Length
import java.io.Serializable
import javax.validation.constraints.Null

data class Account(
        var id: String? = null,
        @get:Null
        var username: String? = null,
        @get:Length(min = 2, max = 30, message = "First name length must be between 2 and 30 characters")
        var firstName: String? = null,
        @get:Length(min = 2, max = 40, message = "Last name length must be between 2 and 40 characters")
        var lastName: String? = null
) : Serializable {

    companion object {

        fun fromUser(user: User): Account {
            return Account(
                    id = user.id,
                    username = user.email,
                    firstName = user.firstName,
                    lastName = user.lastName
            )
        }

    }

}