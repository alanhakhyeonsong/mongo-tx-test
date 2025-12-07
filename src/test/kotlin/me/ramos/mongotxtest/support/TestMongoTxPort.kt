package me.ramos.mongotxtest.support

import me.ramos.mongotxtest.config.MongoTxPort

object TestMongoTxPort : MongoTxPort {
    override fun <T> writeable(function: () -> T): T = function()

    override fun <T> requiresNew(function: () -> T): T = function()
}
