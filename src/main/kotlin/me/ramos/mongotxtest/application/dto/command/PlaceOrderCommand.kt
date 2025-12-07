package me.ramos.mongotxtest.application.dto.command

data class PlaceOrderCommand(
    val clientOrderId: String,
    val items: List<OrderItemCommand>,
)

data class OrderItemCommand(
    val productCode: String,
    val quantity: Int,
)
