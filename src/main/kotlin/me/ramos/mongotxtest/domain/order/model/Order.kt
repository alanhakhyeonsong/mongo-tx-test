package me.ramos.mongotxtest.domain.order.model

import java.time.ZonedDateTime
import me.ramos.mongotxtest.domain.order.enums.OrderStatusKind
import me.ramos.mongotxtest.domain.order.exception.InvalidOrderException

data class Order(
    val id: OrderId,
    val items: List<OrderItem>,
    val status: OrderStatusKind,
    val createdAt: ZonedDateTime,
) {
    init {
        require(items.isNotEmpty()) { "order items must not be empty" }
        if (items.any { it.quantity <= 0 }) {
            throw InvalidOrderException("Quantity must be greater than zero")
        }
    }

    fun totalQuantity(): Int = items.sumOf { it.quantity }
}
