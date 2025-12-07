package me.ramos.mongotxtest.application.port.outbound

import me.ramos.mongotxtest.domain.inventory.model.Inventory

fun interface InventoryLoadPort {
    fun load(productCode: String): Inventory?
}
