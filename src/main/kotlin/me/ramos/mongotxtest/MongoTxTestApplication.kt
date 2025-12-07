package me.ramos.mongotxtest

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MongoTxTestApplication

fun main(args: Array<String>) {
    runApplication<MongoTxTestApplication>(*args)
}
