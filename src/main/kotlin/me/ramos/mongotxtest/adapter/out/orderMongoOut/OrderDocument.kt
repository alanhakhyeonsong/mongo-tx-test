package me.ramos.mongotxtest.adapter.out.orderMongoOut

import java.time.Instant
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.core.mapping.FieldType

@Document(collection = "orders")
@TypeAlias("order")
data class OrderDocument(
    @Id
    val id: String,
    val status: String,
    val items: List<OrderItemDocument>,
    @Field(targetType = FieldType.DATE_TIME)
    val createdAt: Instant,
)

data class OrderItemDocument(
    val productCode: String,
    val quantity: Int,
)
