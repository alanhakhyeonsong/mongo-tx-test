package me.ramos.mongotxtest.application.usecase

import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger
import me.ramos.mongotxtest.application.dto.command.BulkInsertOrderCommand
import me.ramos.mongotxtest.application.dto.result.BulkInsertResult
import me.ramos.mongotxtest.application.port.inbound.BulkOrderInsertInPort
import me.ramos.mongotxtest.application.port.outbound.OrderCommandPort
import me.ramos.mongotxtest.config.MongoTxPort
import me.ramos.mongotxtest.domain.order.enums.OrderStatusKind
import me.ramos.mongotxtest.domain.order.model.Order
import me.ramos.mongotxtest.domain.order.model.OrderId
import me.ramos.mongotxtest.domain.order.model.OrderItem
import org.springframework.stereotype.Service

@Service
class BulkOrderInsertUseCase(
    private val orderCommandPort: OrderCommandPort,
    private val mongoTxPort: MongoTxPort,
) : BulkOrderInsertInPort {

    override fun insert(command: BulkInsertOrderCommand): BulkInsertResult {
        require(command.batchSize > 0) { "batchSize must be positive" }
        val chunkSize = command.chunkSize.takeIf { it > 0 } ?: DEFAULT_CHUNK_SIZE
        val counter = AtomicInteger()
        val orders = (1..command.batchSize).map { buildOrder(counter.incrementAndGet()) }

        orders.chunked(chunkSize).forEach { chunk ->
            mongoTxPort.writeable {
                orderCommandPort.saveAll(chunk)
            }
        }

        return BulkInsertResult(insertedCount = orders.size)
    }

    private fun buildOrder(sequence: Int): Order {
        val now = ZonedDateTime.now(ZoneOffset.UTC)
        val items = listOf(
            OrderItem(productCode = "SKU-${sequence % 10}", quantity = ((sequence % 5) + 1)),
            OrderItem(productCode = "SKU-${(sequence + 1) % 10}", quantity = 1),
        )
        return Order(
            id = OrderId(UUID.randomUUID().toString()),
            items = items,
            status = OrderStatusKind.CREATED,
            createdAt = now,
        )
    }

    companion object {
        private const val DEFAULT_CHUNK_SIZE = 500
    }
}
