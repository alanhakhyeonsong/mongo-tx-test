package me.ramos.mongotxtest.application.port.outbound

import me.ramos.mongotxtest.domain.account.model.PointLedger

fun interface PointLedgerCommandPort {
    fun save(ledger: PointLedger)
}
