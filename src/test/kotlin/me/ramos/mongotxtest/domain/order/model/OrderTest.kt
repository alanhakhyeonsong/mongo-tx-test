package me.ramos.mongotxtest.domain.order.model

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import java.time.ZonedDateTime
import me.ramos.mongotxtest.domain.order.enums.OrderStatusKind
import me.ramos.mongotxtest.domain.order.exception.InvalidOrderException

class OrderTest : BehaviorSpec({
    Given("주문 엔티티가") {
        When("유효한 아이템 목록으로 생성되면") {
            val order = Order(
                id = OrderId("order-1"),
                items = listOf(OrderItem("SKU-1", 2), OrderItem("SKU-2", 3)),
                status = OrderStatusKind.CREATED,
                createdAt = ZonedDateTime.now(),
            )

            Then("총 수량을 정확히 계산한다") {
                order.totalQuantity() shouldBe 5
            }
        }

        When("아이템이 비어있다면") {
            Then("예외가 발생한다") {
                shouldThrow<IllegalArgumentException> {
                    Order(
                        id = OrderId("order-empty"),
                        items = emptyList(),
                        status = OrderStatusKind.CREATED,
                        createdAt = ZonedDateTime.now(),
                    )
                }
            }
        }

        When("아이템 수량이 0 이하라면") {
            Then("InvalidOrderException 이 발생한다") {
                shouldThrow<InvalidOrderException> {
                    Order(
                        id = OrderId("order-invalid"),
                        items = listOf(OrderItem("SKU-1", 0)),
                        status = OrderStatusKind.CREATED,
                        createdAt = ZonedDateTime.now(),
                    )
                }
            }
        }
    }
})
