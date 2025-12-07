package me.ramos.mongotxtest.adapter.out.accountMongoOut

import org.springframework.data.mongodb.repository.MongoRepository

interface AccountMongoRepository : MongoRepository<AccountDocument, String>
