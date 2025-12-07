package me.ramos.mongotxtest.application.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import java.time.ZoneOffset
import java.time.ZonedDateTime
import me.ramos.mongotxtest.application.dto.command.TransferPointCommand
import me.ramos.mongotxtest.application.port.outbound.AccountLoadPort
import me.ramos.mongotxtest.application.port.outbound.AccountSavePort
import me.ramos.mongotxtest.application.port.outbound.PointLedgerCommandPort
import me.ramos.mongotxtest.domain.account.exception.InsufficientPointException
import me.ramos.mongotxtest.domain.account.model.Account
import me.ramos.mongotxtest.domain.account.model.AccountId
import me.ramos.mongotxtest.support.TestMongoTxPort

class TransferPointUseCaseTest : BehaviorSpec({
    Given("포인트 이체 유스케이스가") {
        val accountLoadPort = mockk<AccountLoadPort>()
        val accountSavePort = mockk<AccountSavePort>()
        val pointLedgerCommandPort = mockk<PointLedgerCommandPort>()

        val useCase = TransferPointUseCase(
            accountLoadPort = accountLoadPort,
            accountSavePort = accountSavePort,
            pointLedgerCommandPort = pointLedgerCommandPort,
            mongoTxPort = TestMongoTxPort,
        )

        val fromAccount = Account(AccountId("A-1"), "보낸사람", 10_000, ZonedDateTime.now(ZoneOffset.UTC))
        val toAccount = Account(AccountId("A-2"), "받는사람", 1_000, ZonedDateTime.now(ZoneOffset.UTC))

        When("충분한 포인트를 보유하고 있을 때") {
            every { accountLoadPort.load(AccountId("A-1")) } returns fromAccount
            every { accountLoadPort.load(AccountId("A-2")) } returns toAccount
            justRun { accountSavePort.save(any()) }
            justRun { pointLedgerCommandPort.save(any()) }

            val result = useCase.transfer(TransferPointCommand("A-1", "A-2", 5_000))

            Then("양 계좌가 모두 업데이트되고 원장에 기록된다") {
                result.amount shouldBe 5_000
                verify(exactly = 2) { accountSavePort.save(any()) }
                verify(exactly = 1) { pointLedgerCommandPort.save(any()) }
            }
        }

        When("포인트가 부족하면") {
            every { accountLoadPort.load(AccountId("A-1")) } returns fromAccount.copy(pointBalance = 1_000)
            every { accountLoadPort.load(AccountId("A-2")) } returns toAccount

            Then("예외가 발생한다") {
                shouldThrow<InsufficientPointException> {
                    useCase.transfer(TransferPointCommand("A-1", "A-2", 5_000))
                }
            }
        }
    }
})
