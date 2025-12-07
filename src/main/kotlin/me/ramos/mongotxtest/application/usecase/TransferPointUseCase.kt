package me.ramos.mongotxtest.application.usecase

import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.UUID
import me.ramos.mongotxtest.application.dto.command.TransferPointCommand
import me.ramos.mongotxtest.application.dto.result.TransferPointResult
import me.ramos.mongotxtest.application.port.inbound.TransferPointInPort
import me.ramos.mongotxtest.application.port.outbound.AccountLoadPort
import me.ramos.mongotxtest.application.port.outbound.AccountSavePort
import me.ramos.mongotxtest.application.port.outbound.PointLedgerCommandPort
import me.ramos.mongotxtest.config.MongoTxPort
import me.ramos.mongotxtest.domain.account.model.AccountId
import me.ramos.mongotxtest.domain.account.model.PointLedger
import org.springframework.stereotype.Service

@Service
class TransferPointUseCase(
    private val accountLoadPort: AccountLoadPort,
    private val accountSavePort: AccountSavePort,
    private val pointLedgerCommandPort: PointLedgerCommandPort,
    private val mongoTxPort: MongoTxPort,
) : TransferPointInPort {

    override fun transfer(command: TransferPointCommand): TransferPointResult = mongoTxPort.writeable {
        val now = ZonedDateTime.now(ZoneOffset.UTC)
        val fromAccountId = AccountId(command.fromAccountId)
        val toAccountId = AccountId(command.toAccountId)

        val from = accountLoadPort.load(fromAccountId)
            ?: error("송신 계좌를 찾을 수 없습니다. id=${command.fromAccountId}")
        val to = accountLoadPort.load(toAccountId)
            ?: error("수신 계좌를 찾을 수 없습니다. id=${command.toAccountId}")

        val debited = from.withdraw(command.amount, now)
        val credited = to.deposit(command.amount, now)
        accountSavePort.save(debited)
        accountSavePort.save(credited)

        val ledger = PointLedger(
            id = UUID.randomUUID().toString(),
            fromAccountId = debited.id,
            toAccountId = credited.id,
            amount = command.amount,
            createdAt = now,
        )
        pointLedgerCommandPort.save(ledger)

        TransferPointResult(
            fromAccountId = debited.id.value,
            toAccountId = credited.id.value,
            amount = command.amount,
        )
    }
}
