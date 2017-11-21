package com.scherule.users.controllers.resources

import com.scherule.users.domain.models.Account
import org.springframework.hateoas.ResourceSupport
import org.springframework.hateoas.mvc.ResourceAssemblerSupport
import org.springframework.stereotype.Component


data class AccountResource(
        val id: String,
        val username: String
) : ResourceSupport()

@Component
class AccountResourceAssembler : ResourceAssemblerSupport<Account, AccountResource>(
        Account::class.java,
        AccountResource::class.java
) {

    override fun toResource(account: Account) = AccountResource(
            id = account.id!!,
            username = account.username!!
    )

}