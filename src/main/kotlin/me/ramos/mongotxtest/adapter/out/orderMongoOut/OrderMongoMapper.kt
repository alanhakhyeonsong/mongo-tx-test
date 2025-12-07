package me.ramos.mongotxtest.adapter.out.orderMongoOut

import java.time.ZoneOffset
import java.time.ZonedDateTime
import me.ramos.mongotxtest.domain.order.enums.OrderStatusKind
import me.ramos.mongotxtest.domain.order.model.Order
import me.ramos.mongotxtest.domain.order.model.OrderId
import me.ramos.mongotxtest.domain.order.model.OrderItem
import org.springframework.stereotype.Component

@Component
class OrderMongoMapper {
    fun toDocument(order: Order): OrderDocument =
        OrderDocument(
            id = order.id.value,
            status = order.status.name,
            items = order.items.map { OrderItemDocument(it.productCode, it.quantity) },
            createdAt = order.createdAt.toInstant(),
        )

    fun toDomain(document: OrderDocument): Order =
        Order(
            id = OrderId(document.id),
            items = document.items.map { OrderItem(it.productCode, it.quantity) },
            status = OrderStatusKind.valueOf(document.status),
            createdAt = ZonedDateTime.ofInstant(document.createdAt, ZoneOffset.UTC),
        )
}
