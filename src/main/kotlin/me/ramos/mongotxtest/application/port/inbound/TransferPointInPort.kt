package me.ramos.mongotxtest.application.port.inbound

import me.ramos.mongotxtest.application.dto.command.TransferPointCommand
import me.ramos.mongotxtest.application.dto.result.TransferPointResult

fun interface TransferPointInPort {
    fun transfer(command: TransferPointCommand): TransferPointResult
}
