package me.ramos.mongotxtest.config

interface MongoTxPort {
    fun <T> writeable(function: () -> T): T

    fun <T> requiresNew(function: () -> T): T
}