package me.ramos.mongotxtest.domain.inventory.exception

class InsufficientStockException(productCode: String) : RuntimeException(
    "재고가 부족합니다. productCode=$productCode",
)
