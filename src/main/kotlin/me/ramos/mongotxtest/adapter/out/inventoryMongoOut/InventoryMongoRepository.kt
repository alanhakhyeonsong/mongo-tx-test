package me.ramos.mongotxtest.adapter.out.inventoryMongoOut

import org.springframework.data.mongodb.repository.MongoRepository

interface InventoryMongoRepository : MongoRepository<InventoryDocument, String>
