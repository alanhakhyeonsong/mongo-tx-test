package me.ramos.mongotxtest.domain.account.model

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import java.time.ZonedDateTime
import me.ramos.mongotxtest.domain.account.exception.InsufficientPointException

class AccountTest : BehaviorSpec({
    val baseTime = ZonedDateTime.now()
    val account = Account(AccountId("A-1"), "테스터", 10_000, baseTime)

    Given("계좌 엔티티가") {
        When("포인트를 출금하면") {
            val updated = account.withdraw(3_000, baseTime.plusMinutes(1))

            Then("잔액이 감소한다") {
                updated.pointBalance shouldBe 7_000
            }
        }

        When("포인트를 입금하면") {
            val updated = account.deposit(2_000, baseTime.plusMinutes(1))

            Then("잔액이 증가한다") {
                updated.pointBalance shouldBe 12_000
            }
        }

        When("잔액보다 큰 금액을 출금하면") {
            Then("InsufficientPointException 이 발생한다") {
                shouldThrow<InsufficientPointException> { account.withdraw(20_000, baseTime) }
            }
        }

        When("0 이하 금액으로 입출금하면") {
            Then("IllegalArgumentException 이 발생한다") {
                shouldThrow<IllegalArgumentException> { account.withdraw(0, baseTime) }
                shouldThrow<IllegalArgumentException> { account.deposit(-1, baseTime) }
            }
        }
    }
})
