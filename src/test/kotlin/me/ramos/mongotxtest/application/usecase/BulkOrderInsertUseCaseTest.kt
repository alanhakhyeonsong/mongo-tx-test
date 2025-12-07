package me.ramos.mongotxtest.application.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import me.ramos.mongotxtest.application.dto.command.BulkInsertOrderCommand
import me.ramos.mongotxtest.application.port.outbound.OrderCommandPort
import me.ramos.mongotxtest.domain.order.model.Order
import me.ramos.mongotxtest.support.TestMongoTxPort

class BulkOrderInsertUseCaseTest : BehaviorSpec({
    Given("대량 주문 삽입 유스케이스가") {
        val recordingPort = RecordingOrderPort()
        val useCase = BulkOrderInsertUseCase(recordingPort, TestMongoTxPort)

        When("batchSize 가 1000, chunkSize 가 200 일 때") {
            val result = useCase.insert(BulkInsertOrderCommand(batchSize = 1000, chunkSize = 200))

            Then("청크 수와 크기가 기대와 같다") {
                result.insertedCount shouldBe 1000
                recordingPort.savedBatches.shouldHaveSize(5)
                recordingPort.savedBatches.forEach { it.size shouldBe 200 }
            }
        }

        When("batchSize 가 0 이하라면") {
            Then("예외가 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    useCase.insert(BulkInsertOrderCommand(batchSize = 0, chunkSize = 100))
                }
            }
        }
    }
})

private class RecordingOrderPort : OrderCommandPort {
    val savedBatches = mutableListOf<List<Order>>()

    override fun save(order: Order): Order = order

    override fun saveAll(orders: List<Order>) {
        savedBatches.add(orders)
    }
}
