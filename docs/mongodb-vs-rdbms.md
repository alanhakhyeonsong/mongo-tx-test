# MongoDB 사용 가이드 (Kotlin + Spring Data)

RDBMS(MySQL/PostgreSQL)와 MongoDB의 구조적 차이, Kotlin + Spring Data MongoDB 개발 시 주의점, 흔한 오해, 추가 학습 항목을 모두 정리했습니다. 사내 온보딩/레퍼런스 문서로 활용할 수 있습니다.

## 1. MongoDB vs RDBMS 한눈에 보기

| 항목 | RDBMS | MongoDB |
| --- | --- | --- |
| 저장 단위 | Table / Row | Collection / Document (BSON) |
| 스키마 | 고정(schema-on-write) | 동적(schema-on-read). 단, 애플리케이션이 스키마를 강제해야 함 |
| JOIN | 강력한 JOIN, 옵티마이저 지원 | `$lookup`만 존재. 비용이 크므로 embed 권장 |
| 트랜잭션 | 기본 제공 | Replica Set 이상에서 multi-document TX 가능. 비용 큼 |
| 정규화 | 3정규형 기반 | 반정규화/Embed 기반 |
| 확장성 | Scale-Up 중심 | Scale-Out/샤딩 중심 |
| ID 전략 | AUTO_INCREMENT, UUID 등 | 기본은 `ObjectId`, 하지만 도메인 키를 `_id`로 직접 사용 가능 |

MongoDB는 관계형 모델이 아니기 때문에 “정규화 → JOIN → 트랜잭션” 사고방식이 그대로 적용되지 않습니다. 읽기 패턴을 기반으로 도큐먼트 구조를 설계하고, 함께 사용하는 데이터는 하나의 문서에 embed 하는 것이 기본입니다.

## 2. 핵심 개념 및 주의사항

### 2.1 Document 모델링 (Embed vs Reference)
- **Embed(기본 전략)**: 함께 읽는 데이터는 최대한 같은 문서에 배치. 단일 트랜잭션, 단일 조회로 해결 가능.
- **Reference(예외 전략)**: 문서가 16MB를 초과할 위험이 있거나 N:N 관계가 필수일 때만 참조 저장.

### 2.2 문서 크기(16MB)와 Growth 패턴
- 로그, history, 대용량 리스트를 하나의 문서에 무한히 append 하면 저장 실패가 발생합니다.
- 반복적으로 리스트가 커지는 구조는 별도 컬렉션으로 분리하거나 TTL 컬렉션 활용.

### 2.3 Write Concern / Read Concern
- `writeConcern=majority` vs `1`, `readConcern=local/majority/linearizable` 등으로 일관성을 직접 제어합니다.
- Spring Data에서는 `MongoTemplate` 혹은 `MongoClientSettings` 수준에서 Concern 설정을 명시합니다.

### 2.4 트랜잭션
- 단일 문서 쓰기는 기본적으로 atomic.
- Replica Set 또는 Sharded Cluster에서만 multi-document 트랜잭션이 가능하며, 60초 제한/Abort 가능성이 있습니다.
- Connection String에 `replicaSet` 옵션을 넣고, `MongoTransactionManager` + `@Transactional`을 정확히 구성해야 합니다.

### 2.5 트랜잭션 남용 금지
- MongoDB는 트랜잭션을 “필요할 때만” 쓰도록 설계된 DB입니다. 복잡한 트랜잭션이 일상이면 RDBMS 설계가 맞습니다.
- 단일 문서에 필요한 데이터를 embed 하여 document-level atomic으로 해결하는 것이 정석입니다.

### 2.6 인덱스 설계가 성능의 90%
- 단일, 복합, Partial, TTL, Sparse 인덱스를 적절히 설계해야 합니다.
- MongoDB 옵티마이저는 RDBMS 수준으로 똑똑하지 않아 쿼리마다 명확히 인덱스를 설계하고 `explain()`을 반드시 확인합니다.

### 2.7 페이징과 정렬
- `skip/limit` 기반 오프셋 페이징은 대량 컬렉션에서 매우 비효율적입니다. `_id` 또는 `createdAt` 기반 커서 페이징(Bookmark 방식)을 설계합니다.

### 2.8 Aggregation Pipeline
- MongoDB의 “쿼리 + 집계 + 가공”은 Aggregation Pipeline이 담당합니다.
- `$match`, `$group`, `$project`, `$unwind`, `$facet`, `$lookup` 등을 조합하며, Pipeline 단계 순서가 성능에 매우 중요합니다.

## 3. Kotlin + Spring Data MongoDB 개발 시 체크리스트

### 3.1 Repository/Template 전략
- `MongoRepository`는 JPA와 달리 JPQL/연관관계/LAZY 개념이 없습니다. 복잡한 쿼리는 `MongoTemplate` 또는 `Aggregation`을 사용합니다.

### 3.2 Kotlin data class 설계
- `_id` 필드는 nullable로 정의하여 insert 시 자동 세팅되게 합니다.
- 불변 객체를 유지하려면 `.copy()` 기반 업데이트, 리스트/맵 필드는 전체 교체가 기본임을 인지합니다.
- `@Document`는 컬렉션 명시일 뿐 스키마를 강제하지 않습니다. 반드시 DTO/VO로 의미를 명확히 하고 필요한 경우 Schema Validator를 사용합니다.

### 3.3 Auditing/Converters
- `@EnableMongoAuditing` 설정 후 `@CreatedDate`, `@LastModifiedDate` 를 사용합니다.
- Kotlin 특수 타입(`inline class`, `sealed class`)은 커스텀 Converter를 등록합니다.

### 3.4 Reactive vs Blocking
- 리액티브 드라이버와 블로킹 드라이버를 혼용하지 않습니다. 프로젝트 표준을 먼저 정하고 통일합니다.

### 3.5 Bulk Operations
- 대량 Insert/Update/Delete는 `mongoTemplate.bulkOps(BulkMode.UNORDERED, EntityClass::class.java)` 로 실행합니다. 루프에서 `save()` 반복 호출은 금지.

## 4. 백엔드 개발자가 자주 하는 오해

| 오해 | 교정 |
| --- | --- |
| MongoDB에도 RDBMS 수준의 JOIN이 있을 것 | `$lookup`은 있더라도 비용이 크고 Index 활용이 제한적. 함께 쓰는 데이터는 embed 하는 것이 정석. |
| 트랜잭션 있으니 RDBMS와 똑같이 써도 된다 | 트랜잭션은 Replica Set + 세션 기반 + 60초 제한. 비용이 크고 write conflict가 잦음. Document atomic 설계를 우선. |
| 스키마가 자유로우니 필드를 마음대로 넣어도 된다 | 타입/필드 누락 문제가 런타임에서 발생. Schema Validator, 도큐먼트 버전 필드, 마이그레이션 전략이 필요. |
| 인덱스가 없어도 NoSQL이라 빠르다 | 인덱스가 없으면 full scan으로 RDBMS보다 느리다. 모든 주요 쿼리마다 인덱스를 박아야 함. |
| 동일 문서를 동시에 업데이트해도 알아서 merge 된다 | document-level lock이 없어 마지막 write wins. `@Version` 기반 낙관적 락이나 애플리케이션 락이 필요. |
| RDBMS 쿼리를 MongoDB로 그대로 옮기면 된다 | Aggregation 문법과 단계 순서를 이해해야 하며 `$match` → `$group` → `$project` 순서 최적화가 필수. |
| AUTO_INCREMENT PK가 필요하다 | MongoDB는 기본적으로 `ObjectId` 혹은 UUID 사용. Auto increment는 별도 counter 컬렉션 + 트랜잭션으로 관리해야 하며 권장되지 않음. |

## 5. 마이크로서비스/운영 관점 체크리스트

1. **DB/컬렉션 분리**: 하나의 Replica Set이라도 서비스별 DB를 나눠 네임스페이스 충돌을 막습니다.
2. **Connection Pool 튜닝**: `maxPoolSize`, `minPoolSize`, `waitQueueSize`, `socketTimeoutMS`, `connectTimeoutMS`, `maxConnecting` 등을 서비스 트래픽에 맞게 조정합니다.
3. **Index 정책 수립**: 개발 초기부터 인덱스 설계를 문서화하고, 운영 환경에서 인덱스 추가/삭제 시 영향도를 확인합니다.
4. **Aggregation 숙련**: `$match`, `$group`, `$project`, `$unwind`, `$facet`, `$lookup` 등으로 보고/집계를 처리하는 훈련이 필요합니다.
5. **BulkWrite 도입**: 루프 저장 대신 `bulkOps` 로 일괄 처리하여 트랜잭션 부담을 줄입니다.
6. **Schema Evolution 전략**: 문서 버전 필드, 마이그레이션 Job, backward-compatible 필드 설계.
7. **Monitoring**: `mongostat`, `mongotop`, Micrometer Mongo metrics, Slow query log, Performance Advisor 등을 통해 모니터링.

## 6. 추가 학습 항목

- Aggregation Pipeline 전체 문법과 최적화 패턴
- Replica Set / Sharding 구조, Oplog, Failover
- Index 내부 구조 (B-Tree vs B+Tree)와 Prefix 규칙
- WriteConcern/ReadConcern 조합과 운영 전략
- TTL/Sparse/Partial Index, Schema Validator(JSON Schema)
- BulkWrite 및 Update Operators (`$inc`, `$set`, `$push`, `$pull`)
- MongoTemplate 고급 Query, Testcontainers 기반 통합 테스트

## 7. MongoDB를 선택할 때 고려 사항

- **적합**: 읽기 중심 이벤트/로그, JSON-like 데이터, JOIN이 거의 없는 도메인, 빈번한 스키마 변화, 대규모 Scale-Out이 필요한 서비스.
- **부적합**: 복잡한 JOIN/정규화가 필수, 강력한 트랜잭션이 상시 필요한 도메인, 문서 크기가 자주 16MB를 초과하거나 엄격한 스키마가 필요한 경우.

MongoDB를 사용할 때는 “embed 기반 설계, 필요한 곳만 트랜잭션, 인덱스/커넥션 풀/모니터링 선제 설정” 원칙을 기억하고, Kotlin + Spring Data MongoDB의 한계와 특성을 명확히 이해한 상태에서 개발해야 합니다.
