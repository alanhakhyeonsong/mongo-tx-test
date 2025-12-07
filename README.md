# Mongo Transaction Test Sandbox

MongoDB 트랜잭션 기능을 검증하기 위한 Kotlin + Spring Boot 샘플입니다.  
헥사고날 아키텍처를 간략히 적용해 도메인/애플리케이션/어댑터 계층을 분리하고, 대량 삽입과 다중 컬렉션 갱신, 실패 롤백을 손쉽게 실험할 수 있도록 구성했습니다.

## 실행 준비

1. **MongoDB Replica Set**
   ```bash
   docker compose up -d
   ```
   - `docker-compose.yml`은 단일 노드 Replica Set(`rs0`)을 구동합니다.
   - **최초 1회** 컨테이너 내에서 Replica Set을 초기화해야 합니다.
     ```bash
     docker compose exec mongodb mongosh --eval 'rs.initiate({_id:"rs0",members:[{_id:0,host:"localhost:27017"}]})'
     ```
   - 애플리케이션은 `mongodb://localhost:27017/mongo-tx-test?replicaSet=rs0` 로 연결합니다.

2. **애플리케이션 구동**
   ```bash
   ./gradlew bootRun
   ```

3. **샘플 데이터**
   - `inventories`, `accounts` 컬렉션에 초기 데이터를 넣어야 합니다. 간단히 `mongosh`에서 `insertOne` 하거나 `data` 스크립트를 추가하세요.

## 핵심 시나리오

| Use Case | REST Endpoint | 설명 |
| --- | --- | --- |
| 주문 생성 | `POST /api/v1/orders` | 재고 차감 + 주문 생성. 재고 부족 시 롤백 확인 |
| 주문 대량 삽입 | `POST /api/v1/orders/bulk` | 지정한 배치/청크 단위로 수천 건 insert. 동시성/세션 안정성 검증 |
| 포인트 이체 | `POST /api/v1/accounts/transfer` | 서로 다른 컬렉션(`accounts`, `point_ledgers`)에 대해 트랜잭션 처리 |

각 유스케이스는 `MongoTxPort`(Spring `MongoTransactionManager`)를 통해 단일 세션에서 수행됩니다.

## 테스트

```bash
./gradlew test
```

- `Kotest` BehaviorSpec + `MockK` 로 도메인/유스케이스 단위 테스트를 구성했습니다.
- 실제 Mongo 세션을 사용한 통합 테스트는 필요 시 `Testcontainers`로 확장할 수 있습니다.

## 구조

```
src/main/kotlin/me/ramos/mongotxtest
├── domain/         # 순수 도메인 모델 (Order/Inventory/Account)
├── application/    # DTO, Port, UseCase
└── adapter/
    ├── in/         # Web Controller(Request/Response 변환)
    └── out/        # MongoDB Adapter(문서/리포지토리)
```

각 계층은 의존성 방향을 단일 방향으로 유지하여 MongoDB 외 다른 저장소로도 교체 가능하도록 했습니다.

## 추가 점검 포인트

- `application.yml`에서 `maxConnectionPoolSize`, `readConcern`, `writeConcern` 등을 조정해 다양한 부하 조건을 실험할 수 있습니다.
- 대량 삽입 시 `chunkSize` 조절, `@Transactional` 범위 변경, `MongoTemplate` 세션 직접 제어 등도 시나리오 확장에 활용하세요.

필요한 추가 실험이나 통합 테스트 시나리오가 있으면 이 README를 확장하면서 문서화하면 됩니다.
