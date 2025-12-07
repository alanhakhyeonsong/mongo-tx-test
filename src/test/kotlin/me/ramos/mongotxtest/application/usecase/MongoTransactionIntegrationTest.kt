package me.ramos.mongotxtest.application.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.core.spec.IsolationMode
import me.ramos.mongotxtest.adapter.out.inventoryMongoOut.InventoryDocument
import me.ramos.mongotxtest.adapter.out.inventoryMongoOut.InventoryMongoRepository
import me.ramos.mongotxtest.adapter.out.orderMongoOut.OrderMongoRepository
import me.ramos.mongotxtest.application.dto.command.OrderItemCommand
import me.ramos.mongotxtest.application.dto.command.PlaceOrderCommand
import me.ramos.mongotxtest.application.port.inbound.PlaceOrderInPort
import me.ramos.mongotxtest.domain.inventory.exception.InsufficientStockException
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.junit.jupiter.api.Assumptions
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.utility.DockerImageName

@SpringBootTest
class MongoTransactionIntegrationTest(
    private val placeOrderInPort: PlaceOrderInPort,
    private val inventoryMongoRepository: InventoryMongoRepository,
    private val orderMongoRepository: OrderMongoRepository,
) : BehaviorSpec({

        isolationMode = IsolationMode.InstancePerLeaf

        Given("MongoDB 트랜잭션 환경에서") {
            When("모든 재고가 충분하면") {
                orderMongoRepository.deleteAll()
                inventoryMongoRepository.deleteAll()
                inventoryMongoRepository.save(InventoryDocument("SKU-IT-1", 5))

                val result = placeOrderInPort.place(
                    PlaceOrderCommand(
                        clientOrderId = "order-it-success",
                        items = listOf(OrderItemCommand("SKU-IT-1", 2)),
                    ),
                )

                Then("주문이 저장되고 재고가 차감된다") {
                    result.totalQuantity shouldBe 2
                    orderMongoRepository.count() shouldBe 1
                    inventoryMongoRepository.findById("SKU-IT-1").get().quantity shouldBe 3
                }
            }

            When("여러 재고 중 하나가 부족하면") {
                orderMongoRepository.deleteAll()
                inventoryMongoRepository.deleteAll()
                inventoryMongoRepository.save(InventoryDocument("SKU-IT-2", 4))
                inventoryMongoRepository.save(InventoryDocument("SKU-IT-3", 1))

                Then("모든 변경이 롤백된다") {
                    shouldThrow<InsufficientStockException> {
                        placeOrderInPort.place(
                            PlaceOrderCommand(
                                clientOrderId = "order-it-fail",
                                items = listOf(
                                    OrderItemCommand("SKU-IT-2", 2),
                                    OrderItemCommand("SKU-IT-3", 5),
                                ),
                            ),
                        )
                    }

                    orderMongoRepository.count() shouldBe 0
                    inventoryMongoRepository.findById("SKU-IT-2").get().quantity shouldBe 4
                    inventoryMongoRepository.findById("SKU-IT-3").get().quantity shouldBe 1
                }
            }
        }
    }) {
    companion object {
        private val dockerAvailable: Boolean = try {
            org.testcontainers.DockerClientFactory.instance().isDockerAvailable
        } catch (ex: Throwable) {
            false
        }
        private var mongo: MongoDBContainer? = null

        @JvmStatic
        @DynamicPropertySource
        fun mongoProperties(registry: DynamicPropertyRegistry) {
            Assumptions.assumeTrue(dockerAvailable, "Docker is required for MongoDB integration test")
            if (mongo == null) {
                mongo = MongoDBContainer(DockerImageName.parse("mongo:7.0")).apply { start() }
            }
            registry.add("spring.data.mongodb.uri") { mongo!!.replicaSetUrl }
        }
    }
}
