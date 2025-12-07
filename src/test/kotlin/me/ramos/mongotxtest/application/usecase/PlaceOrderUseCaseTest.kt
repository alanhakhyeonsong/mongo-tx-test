package me.ramos.mongotxtest.application.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import me.ramos.mongotxtest.application.dto.command.OrderItemCommand
import me.ramos.mongotxtest.application.dto.command.PlaceOrderCommand
import me.ramos.mongotxtest.application.port.outbound.InventoryLoadPort
import me.ramos.mongotxtest.application.port.outbound.InventorySavePort
import me.ramos.mongotxtest.application.port.outbound.OrderCommandPort
import me.ramos.mongotxtest.domain.inventory.exception.InsufficientStockException
import me.ramos.mongotxtest.domain.inventory.model.Inventory
import me.ramos.mongotxtest.domain.order.enums.OrderStatusKind
import me.ramos.mongotxtest.domain.order.model.Order
import me.ramos.mongotxtest.support.TestMongoTxPort

class PlaceOrderUseCaseTest : BehaviorSpec({
    Given("주문 생성 유스케이스가") {
        val orderCommandPort = mockk<OrderCommandPort>()
        val inventoryLoadPort = mockk<InventoryLoadPort>()
        val inventorySavePort = mockk<InventorySavePort>()

        When("충분한 재고가 있을 때") {
            val useCase = PlaceOrderUseCase(
                orderCommandPort = orderCommandPort,
                inventoryLoadPort = inventoryLoadPort,
                inventorySavePort = inventorySavePort,
                mongoTxPort = TestMongoTxPort,
            )
            val inventory = Inventory("SKU-1", 10)
            every { inventoryLoadPort.load("SKU-1") } returns inventory
            justRun { inventorySavePort.save(any()) }
            every { orderCommandPort.save(any()) } answers { firstArg() as Order }

            val result = useCase.place(
                PlaceOrderCommand(
                    clientOrderId = "order-1",
                    items = listOf(OrderItemCommand("SKU-1", 2)),
                ),
            )

            Then("재고가 차감되고 주문이 저장된다") {
                result.status shouldBe OrderStatusKind.CREATED
                verify { inventorySavePort.save(match { it.quantity == 8 }) }
                verify { orderCommandPort.save(match { it.items.size == 1 }) }
            }
        }

        When("필요 수량보다 재고가 적으면") {
            val useCase = PlaceOrderUseCase(
                orderCommandPort = orderCommandPort,
                inventoryLoadPort = inventoryLoadPort,
                inventorySavePort = inventorySavePort,
                mongoTxPort = TestMongoTxPort,
            )
            every { inventoryLoadPort.load("SKU-2") } returns Inventory("SKU-2", 1)

            Then("예외가 발생한다") {
                shouldThrow<InsufficientStockException> {
                    useCase.place(
                        PlaceOrderCommand(
                            clientOrderId = "order-2",
                            items = listOf(OrderItemCommand("SKU-2", 5)),
                        ),
                    )
                }
            }
        }
    }
})
