package me.ramos.mongotxtest.domain.account.model

import java.time.ZonedDateTime
import me.ramos.mongotxtest.domain.account.exception.InsufficientPointException

data class Account(
    val id: AccountId,
    val ownerName: String,
    val pointBalance: Long,
    val updatedAt: ZonedDateTime,
) {
    init {
        require(pointBalance >= 0) { "point balance must not be negative" }
    }

    fun withdraw(amount: Long, eventTime: ZonedDateTime): Account {
        validateAmount(amount)
        if (amount > pointBalance) {
            throw InsufficientPointException(id.value)
        }
        return copy(pointBalance = pointBalance - amount, updatedAt = eventTime)
    }

    fun deposit(amount: Long, eventTime: ZonedDateTime): Account {
        validateAmount(amount)
        return copy(pointBalance = pointBalance + amount, updatedAt = eventTime)
    }

    private fun validateAmount(amount: Long) {
        require(amount > 0) { "amount must be positive" }
    }
}
