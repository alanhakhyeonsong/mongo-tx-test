package me.ramos.mongotxtest.adapter.`in`.orderWebIn

import me.ramos.mongotxtest.application.dto.command.BulkInsertOrderCommand
import me.ramos.mongotxtest.application.dto.command.OrderItemCommand
import me.ramos.mongotxtest.application.dto.command.PlaceOrderCommand
import me.ramos.mongotxtest.application.port.inbound.BulkOrderInsertInPort
import me.ramos.mongotxtest.application.port.inbound.PlaceOrderInPort
import me.ramos.mongotxtest.domain.order.enums.OrderStatusKind
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/orders")
class OrderV1Controller(
    private val placeOrderInPort: PlaceOrderInPort,
    private val bulkOrderInsertInPort: BulkOrderInsertInPort,
) {

    @PostMapping
    fun place(@RequestBody request: PlaceOrderRequest): ResponseEntity<OrderResponse> {
        val command = PlaceOrderCommand(
            clientOrderId = request.clientOrderId.orEmpty(),
            items = request.items.map { OrderItemCommand(it.productCode, it.quantity) },
        )
        val result = placeOrderInPort.place(command)
        val response = OrderResponse(result.orderId, result.status, result.totalQuantity)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/bulk")
    fun bulk(@RequestBody request: BulkInsertRequest): ResponseEntity<BulkInsertResponse> {
        val command = BulkInsertOrderCommand(
            batchSize = request.batchSize,
            chunkSize = request.chunkSize,
        )
        val result = bulkOrderInsertInPort.insert(command)
        return ResponseEntity.ok(BulkInsertResponse(result.insertedCount))
    }
}

data class PlaceOrderRequest(
    val clientOrderId: String?,
    val items: List<OrderItemRequest>,
)

data class OrderItemRequest(
    val productCode: String,
    val quantity: Int,
)

data class OrderResponse(
    val orderId: String,
    val status: OrderStatusKind,
    val totalQuantity: Int,
)

data class BulkInsertRequest(
    val batchSize: Int,
    val chunkSize: Int = 500,
)

data class BulkInsertResponse(
    val insertedCount: Int,
)
