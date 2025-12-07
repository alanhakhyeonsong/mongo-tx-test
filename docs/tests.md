# 테스트 케이스

## 단위 테스트 (Kotest BehaviorSpec)

| Test Class | 시나리오 | 설명 |
| --- | --- | --- |
| `PlaceOrderUseCaseTest` | "충분한 재고" | 재고가 충분할 때 재고 저장 + 주문 저장이 1회씩 호출되는지 검증 |
| `PlaceOrderUseCaseTest` | "재고 부족" | `InsufficientStockException` 발생 여부 확인 |
| `TransferPointUseCaseTest` | "충분한 포인트" | 출금/입금이 각각 저장되고 원장이 기록되는지 검증 |
| `TransferPointUseCaseTest` | "포인트 부족" | `InsufficientPointException` 발생 여부 확인 |

모든 테스트는 `MockK`를 사용하여 포트를 목킹하고, `TestMongoTxPort`로 트랜잭션 경계를 간단히 대체했습니다.

## 향후 확장 아이디어

- **Integration Test**: `@DataMongoTest` + `MongoTemplate` 으로 실제 세션 기반 트랜잭션을 검증.
- **Load Test**: `BulkOrderInsertUseCase`를 이용해 `kotest-property` 기반 property-based 테스트를 도입해 다양한 배치/청크값을 생성.
- **API Contract Test**: `RestAssured`나 `Spring MockMvc`로 REST 스펙을 문서화(`spring-restdocs`)하여 README와 연동.
