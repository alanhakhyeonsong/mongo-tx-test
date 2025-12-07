package me.ramos.mongotxtest.application.port.outbound

import me.ramos.mongotxtest.domain.account.model.Account
import me.ramos.mongotxtest.domain.account.model.AccountId

fun interface AccountLoadPort {
    fun load(accountId: AccountId): Account?
}
