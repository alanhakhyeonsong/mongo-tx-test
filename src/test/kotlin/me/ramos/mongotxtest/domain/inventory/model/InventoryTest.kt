package me.ramos.mongotxtest.domain.inventory.model

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import me.ramos.mongotxtest.domain.inventory.exception.InsufficientStockException

class InventoryTest : BehaviorSpec({
    Given("재고 엔티티가") {
        val inventory = Inventory(productCode = "SKU-1", quantity = 10)

        When("충분한 수량만큼 차감하면") {
            val updated = inventory.decrease(3)

            Then("수량이 감소한다") {
                updated.quantity shouldBe 7
            }
        }

        When("요청 수량이 0 이하이면") {
            Then("IllegalArgumentException 이 발생한다") {
                shouldThrow<IllegalArgumentException> { inventory.decrease(0) }
            }
        }

        When("요청 수량이 재고보다 많으면") {
            Then("InsufficientStockException 이 발생한다") {
                shouldThrow<InsufficientStockException> { inventory.decrease(20) }
            }
        }
    }
})
