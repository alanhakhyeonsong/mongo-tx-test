package me.ramos.mongotxtest.application.port.outbound

import me.ramos.mongotxtest.domain.account.model.Account

fun interface AccountSavePort {
    fun save(account: Account)
}
