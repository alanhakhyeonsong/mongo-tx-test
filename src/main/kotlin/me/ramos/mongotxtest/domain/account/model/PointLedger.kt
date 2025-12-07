package me.ramos.mongotxtest.domain.account.model

import java.time.ZonedDateTime

data class PointLedger(
    val id: String,
    val fromAccountId: AccountId,
    val toAccountId: AccountId,
    val amount: Long,
    val createdAt: ZonedDateTime,
)
