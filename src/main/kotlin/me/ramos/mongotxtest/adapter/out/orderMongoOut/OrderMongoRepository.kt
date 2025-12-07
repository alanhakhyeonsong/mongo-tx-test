package me.ramos.mongotxtest.adapter.out.orderMongoOut

import org.springframework.data.mongodb.repository.MongoRepository

interface OrderMongoRepository : MongoRepository<OrderDocument, String>
