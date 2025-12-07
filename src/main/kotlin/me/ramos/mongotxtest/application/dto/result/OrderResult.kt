package me.ramos.mongotxtest.application.dto.result

import me.ramos.mongotxtest.domain.order.enums.OrderStatusKind

data class OrderResult(
    val orderId: String,
    val status: OrderStatusKind,
    val totalQuantity: Int,
)
