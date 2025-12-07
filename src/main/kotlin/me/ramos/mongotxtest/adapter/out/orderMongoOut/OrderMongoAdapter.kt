package me.ramos.mongotxtest.adapter.out.orderMongoOut

import me.ramos.mongotxtest.application.port.outbound.OrderCommandPort
import me.ramos.mongotxtest.domain.order.model.Order
import org.springframework.stereotype.Component

@Component
class OrderMongoAdapter(
    private val repository: OrderMongoRepository,
    private val mapper: OrderMongoMapper,
) : OrderCommandPort {

    override fun save(order: Order): Order {
        val saved = repository.save(mapper.toDocument(order))
        return mapper.toDomain(saved)
    }

    override fun saveAll(orders: List<Order>) {
        if (orders.isEmpty()) return
        repository.saveAll(orders.map(mapper::toDocument))
    }
}
