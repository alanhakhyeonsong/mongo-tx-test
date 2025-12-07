package me.ramos.mongotxtest.adapter.out.ledgerMongoOut

import me.ramos.mongotxtest.application.port.outbound.PointLedgerCommandPort
import me.ramos.mongotxtest.domain.account.model.PointLedger
import org.springframework.stereotype.Component

@Component
class PointLedgerMongoAdapter(
    private val repository: PointLedgerMongoRepository,
    private val mapper: PointLedgerMongoMapper,
) : PointLedgerCommandPort {

    override fun save(ledger: PointLedger) {
        repository.save(mapper.toDocument(ledger))
    }
}
