package me.ramos.mongotxtest.domain.account.exception

class InsufficientPointException(accountId: String) : RuntimeException(
    "포인트가 부족합니다. accountId=$accountId",
)
