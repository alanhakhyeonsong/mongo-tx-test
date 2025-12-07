package me.ramos.mongotxtest.application.port.inbound

import me.ramos.mongotxtest.application.dto.command.BulkInsertOrderCommand
import me.ramos.mongotxtest.application.dto.result.BulkInsertResult

fun interface BulkOrderInsertInPort {
    fun insert(command: BulkInsertOrderCommand): BulkInsertResult
}
