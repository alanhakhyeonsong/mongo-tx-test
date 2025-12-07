package me.ramos.mongotxtest.adapter.out.ledgerMongoOut

import java.time.Instant
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.core.mapping.FieldType

@Document(collection = "point_ledgers")
@TypeAlias("pointLedger")
data class PointLedgerDocument(
    @Id
    val id: String,
    val fromAccountId: String,
    val toAccountId: String,
    val amount: Long,
    @Field(targetType = FieldType.DATE_TIME)
    val createdAt: Instant,
)
