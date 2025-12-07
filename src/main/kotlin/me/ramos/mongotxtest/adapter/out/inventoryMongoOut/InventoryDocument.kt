package me.ramos.mongotxtest.adapter.out.inventoryMongoOut

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "inventories")
@TypeAlias("inventory")
data class InventoryDocument(
    @Id
    val productCode: String,
    val quantity: Int,
)
