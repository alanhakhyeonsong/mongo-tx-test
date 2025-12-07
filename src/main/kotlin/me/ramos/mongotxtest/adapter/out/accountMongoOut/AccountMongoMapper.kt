package me.ramos.mongotxtest.adapter.out.accountMongoOut

import java.time.ZoneOffset
import java.time.ZonedDateTime
import me.ramos.mongotxtest.domain.account.model.Account
import me.ramos.mongotxtest.domain.account.model.AccountId
import org.springframework.stereotype.Component

@Component
class AccountMongoMapper {
    fun toDomain(document: AccountDocument): Account =
        Account(
            id = AccountId(document.id),
            ownerName = document.ownerName,
            pointBalance = document.pointBalance,
            updatedAt = ZonedDateTime.ofInstant(document.updatedAt, ZoneOffset.UTC),
        )

    fun toDocument(account: Account): AccountDocument =
        AccountDocument(
            id = account.id.value,
            ownerName = account.ownerName,
            pointBalance = account.pointBalance,
            updatedAt = account.updatedAt.toInstant(),
        )
}
