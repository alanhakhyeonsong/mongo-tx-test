package me.ramos.mongotxtest.adapter.out.accountMongoOut

import me.ramos.mongotxtest.application.port.outbound.AccountLoadPort
import me.ramos.mongotxtest.application.port.outbound.AccountSavePort
import me.ramos.mongotxtest.domain.account.model.Account
import me.ramos.mongotxtest.domain.account.model.AccountId
import org.springframework.stereotype.Component

@Component
class AccountMongoAdapter(
    private val repository: AccountMongoRepository,
    private val mapper: AccountMongoMapper,
) : AccountLoadPort, AccountSavePort {

    override fun load(accountId: AccountId): Account? =
        repository.findById(accountId.value).map(mapper::toDomain).orElse(null)

    override fun save(account: Account) {
        repository.save(mapper.toDocument(account))
    }
}
