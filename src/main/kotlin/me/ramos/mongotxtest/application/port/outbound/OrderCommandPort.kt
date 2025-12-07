package me.ramos.mongotxtest.application.port.outbound

import me.ramos.mongotxtest.domain.order.model.Order

interface OrderCommandPort {
    fun save(order: Order): Order

    fun saveAll(orders: List<Order>)
}
