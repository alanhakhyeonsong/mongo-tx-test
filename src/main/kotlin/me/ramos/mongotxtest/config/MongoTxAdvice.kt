package me.ramos.mongotxtest.config

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation.REQUIRES_NEW
import org.springframework.transaction.annotation.Transactional

@Component
internal class MongoTxAdvice {
    @Transactional(transactionManager = "mongoTransactionManager")
    fun <T> writeable(function: () -> T): T = function.invoke()

    @Transactional(transactionManager = "mongoTransactionManager", propagation = REQUIRES_NEW)
    fun <T> requiresNew(function: () -> T): T = function.invoke()
}