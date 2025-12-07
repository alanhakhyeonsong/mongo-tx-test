package me.ramos.mongotxtest.adapter.out.ledgerMongoOut

import org.springframework.data.mongodb.repository.MongoRepository

interface PointLedgerMongoRepository : MongoRepository<PointLedgerDocument, String>
