package me.ramos.mongotxtest.application.usecase

import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.UUID
import me.ramos.mongotxtest.application.dto.command.PlaceOrderCommand
import me.ramos.mongotxtest.application.dto.result.OrderResult
import me.ramos.mongotxtest.application.port.inbound.PlaceOrderInPort
import me.ramos.mongotxtest.application.port.outbound.InventoryLoadPort
import me.ramos.mongotxtest.application.port.outbound.InventorySavePort
import me.ramos.mongotxtest.application.port.outbound.OrderCommandPort
import me.ramos.mongotxtest.config.MongoTxPort
import me.ramos.mongotxtest.domain.inventory.exception.InsufficientStockException
import me.ramos.mongotxtest.domain.order.enums.OrderStatusKind
import me.ramos.mongotxtest.domain.order.model.Order
import me.ramos.mongotxtest.domain.order.model.OrderId
import me.ramos.mongotxtest.domain.order.model.OrderItem
import org.springframework.stereotype.Service

@Service
class PlaceOrderUseCase(
    private val orderCommandPort: OrderCommandPort,
    private val inventoryLoadPort: InventoryLoadPort,
    private val inventorySavePort: InventorySavePort,
    private val mongoTxPort: MongoTxPort,
) : PlaceOrderInPort {

    override fun place(command: PlaceOrderCommand): OrderResult = mongoTxPort.writeable {
        val now = ZonedDateTime.now(ZoneOffset.UTC)
        val items = command.items.map { OrderItem(it.productCode, it.quantity) }
        val orderIdValue = command.clientOrderId.takeIf { it.isNotBlank() } ?: UUID.randomUUID().toString()

        val requiredQuantities = items
            .groupBy { it.productCode }
            .mapValues { entry -> entry.value.sumOf { it.quantity } }

        requiredQuantities.forEach { (productCode, requiredQuantity) ->
            val inventory = inventoryLoadPort.load(productCode)
                ?: throw InsufficientStockException(productCode)
            val updated = inventory.decrease(requiredQuantity)
            inventorySavePort.save(updated)
        }

        val order = Order(
            id = OrderId(orderIdValue),
            items = items,
            status = OrderStatusKind.CREATED,
            createdAt = now,
        )
        val saved = orderCommandPort.save(order)
        OrderResult(
            orderId = saved.id.value,
            status = saved.status,
            totalQuantity = saved.totalQuantity(),
        )
    }
}
