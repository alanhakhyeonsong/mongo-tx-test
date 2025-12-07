package me.ramos.mongotxtest.adapter.out.accountMongoOut

import java.time.Instant
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.core.mapping.FieldType

@Document(collection = "accounts")
@TypeAlias("account")
data class AccountDocument(
    @Id
    val id: String,
    val ownerName: String,
    val pointBalance: Long,
    @Field(targetType = FieldType.DATE_TIME)
    val updatedAt: Instant,
)
