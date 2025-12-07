# 테스트 케이스

## 단위 테스트 (Kotest BehaviorSpec)

| Test Class | 시나리오 | 설명 |
| --- | --- | --- |
| `OrderTest` | 빈 목록, 음수 수량 | 주문 생성 규칙 위반 시 예외 발생 여부 검증 |
| `InventoryTest` | 잔여 수량/부족 수량 | `decrease` 동작과 예외 처리 검증 |
| `AccountTest` | 입/출금, 음수 포인트 | 포인트 변동과 예외 발생 여부 검증 |
| `PlaceOrderUseCaseTest` | 충분/부족 재고 | 포트 호출 및 예외 발생 검증 |
| `TransferPointUseCaseTest` | 충분/부족 포인트 | 계좌 저장/원장 기록 여부 검증 |
| `BulkOrderInsertUseCaseTest` | 청크 배치 처리 | `batchSize/chunkSize` 조합에 따른 저장 호출 횟수 확인 |

`PlaceOrderUseCaseTest`, `TransferPointUseCaseTest`, `BulkOrderInsertUseCaseTest` 는 `MockK`와 `TestMongoTxPort`로 포트를 목킹해 트랜잭션 경계를 대체합니다.

## 통합 테스트

| Test Class | 시나리오 | 설명 |
| --- | --- | --- |
| `MongoTransactionIntegrationTest` | 성공/부분 실패 주문 | Testcontainers MongoDB Replica Set 상에서 트랜잭션 커밋/롤백 동작 검증 |

- Docker 가 필요하며, 실행 환경에 Docker가 없으면 자동으로 비활성화됩니다.
- `InventoryMongoRepository`, `OrderMongoRepository`, `PlaceOrderInPort`를 실제 빈으로 주입해 엔드투엔드 흐름을 검증합니다.

## 향후 확장 아이디어

- **Load Test**: `BulkOrderInsertUseCase`를 이용해 `kotest-property` 기반 property-based 테스트를 도입해 다양한 배치/청크값을 생성.
- **API Contract Test**: `RestAssured`나 `Spring MockMvc`로 REST 스펙을 문서화(`spring-restdocs`)하여 README와 연동.
