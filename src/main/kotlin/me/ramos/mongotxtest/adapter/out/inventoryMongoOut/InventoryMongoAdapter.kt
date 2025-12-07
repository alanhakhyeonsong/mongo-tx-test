package me.ramos.mongotxtest.adapter.out.inventoryMongoOut

import me.ramos.mongotxtest.application.port.outbound.InventoryLoadPort
import me.ramos.mongotxtest.application.port.outbound.InventorySavePort
import me.ramos.mongotxtest.domain.inventory.model.Inventory
import org.springframework.stereotype.Component

@Component
class InventoryMongoAdapter(
    private val repository: InventoryMongoRepository,
    private val mapper: InventoryMongoMapper,
) : InventoryLoadPort, InventorySavePort {

    override fun load(productCode: String): Inventory? =
        repository.findById(productCode).map(mapper::toDomain).orElse(null)

    override fun save(inventory: Inventory) {
        repository.save(mapper.toDocument(inventory))
    }
}
