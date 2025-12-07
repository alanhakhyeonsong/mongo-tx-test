package me.ramos.mongotxtest.domain.inventory.model

import me.ramos.mongotxtest.domain.inventory.exception.InsufficientStockException

data class Inventory(
    val productCode: String,
    val quantity: Int,
) {
    init {
        require(quantity >= 0) { "inventory quantity must be positive" }
    }

    fun decrease(requested: Int): Inventory {
        if (requested <= 0) {
            throw IllegalArgumentException("requested quantity must be positive")
        }
        if (requested > quantity) {
            throw InsufficientStockException(productCode)
        }
        return copy(quantity = quantity - requested)
    }
}
