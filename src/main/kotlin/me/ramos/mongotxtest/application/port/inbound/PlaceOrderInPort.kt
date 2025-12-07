package me.ramos.mongotxtest.application.port.inbound

import me.ramos.mongotxtest.application.dto.command.PlaceOrderCommand
import me.ramos.mongotxtest.application.dto.result.OrderResult

fun interface PlaceOrderInPort {
    fun place(command: PlaceOrderCommand): OrderResult
}
