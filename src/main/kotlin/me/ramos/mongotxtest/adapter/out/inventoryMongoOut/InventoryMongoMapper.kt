package me.ramos.mongotxtest.adapter.out.inventoryMongoOut

import me.ramos.mongotxtest.domain.inventory.model.Inventory
import org.springframework.stereotype.Component

@Component
class InventoryMongoMapper {
    fun toDomain(document: InventoryDocument): Inventory =
        Inventory(
            productCode = document.productCode,
            quantity = document.quantity,
        )

    fun toDocument(inventory: Inventory): InventoryDocument =
        InventoryDocument(
            productCode = inventory.productCode,
            quantity = inventory.quantity,
        )
}
