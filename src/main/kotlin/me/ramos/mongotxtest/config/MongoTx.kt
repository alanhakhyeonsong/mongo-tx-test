package me.ramos.mongotxtest.config

import org.springframework.stereotype.Component

@Component
internal class MongoTx(
    private val impMongoTxAdvice: MongoTxAdvice,
) : MongoTxPort {
    override fun <T> writeable(function: () -> T): T = impMongoTxAdvice.writeable(function)

    override fun <T> requiresNew(function: () -> T): T = impMongoTxAdvice.requiresNew(function)
}