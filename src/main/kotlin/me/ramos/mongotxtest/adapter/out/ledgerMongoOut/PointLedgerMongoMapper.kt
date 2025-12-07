package me.ramos.mongotxtest.adapter.out.ledgerMongoOut

import java.time.ZoneOffset
import java.time.ZonedDateTime
import me.ramos.mongotxtest.domain.account.model.AccountId
import me.ramos.mongotxtest.domain.account.model.PointLedger
import org.springframework.stereotype.Component

@Component
class PointLedgerMongoMapper {
    fun toDocument(ledger: PointLedger): PointLedgerDocument =
        PointLedgerDocument(
            id = ledger.id,
            fromAccountId = ledger.fromAccountId.value,
            toAccountId = ledger.toAccountId.value,
            amount = ledger.amount,
            createdAt = ledger.createdAt.toInstant(),
        )

    fun toDomain(document: PointLedgerDocument): PointLedger =
        PointLedger(
            id = document.id,
            fromAccountId = AccountId(document.fromAccountId),
            toAccountId = AccountId(document.toAccountId),
            amount = document.amount,
            createdAt = ZonedDateTime.ofInstant(document.createdAt, ZoneOffset.UTC),
        )
}
