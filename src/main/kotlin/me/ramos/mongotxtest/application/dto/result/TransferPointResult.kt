package me.ramos.mongotxtest.application.dto.result

data class TransferPointResult(
    val fromAccountId: String,
    val toAccountId: String,
    val amount: Long,
)
