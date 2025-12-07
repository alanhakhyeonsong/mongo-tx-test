package me.ramos.mongotxtest.application.dto.command

data class BulkInsertOrderCommand(
    val batchSize: Int,
    val chunkSize: Int,
)
