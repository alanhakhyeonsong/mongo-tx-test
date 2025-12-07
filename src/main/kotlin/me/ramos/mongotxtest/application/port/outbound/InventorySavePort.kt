package me.ramos.mongotxtest.application.port.outbound

import me.ramos.mongotxtest.domain.inventory.model.Inventory

fun interface InventorySavePort {
    fun save(inventory: Inventory)
}
