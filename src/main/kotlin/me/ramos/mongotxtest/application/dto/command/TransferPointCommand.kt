package me.ramos.mongotxtest.application.dto.command

data class TransferPointCommand(
    val fromAccountId: String,
    val toAccountId: String,
    val amount: Long,
)
