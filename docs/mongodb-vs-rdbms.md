# MongoDB vs. RDBMS (MySQL/PostgreSQL)

MongoDB는 문서 지향 NoSQL 데이터베이스로, 전통적인 관계형 DBMS(RDBMS)와 설계 철학과 운영 방식이 다릅니다. Kotlin + Spring Data 환경에서 MongoDB를 사용할 때 알아야 할 차이점과 주의사항을 정리했습니다.

## 1. 데이터 모델링 차이

- **스키마 유연성**: MongoDB는 스키마 강제가 없지만, 컬렉션별로 도큐먼트 구조를 명확히 설계하고 DTO/엔티티로 강제해야 안정적인 애플리케이션을 유지할 수 있습니다. Spring Data MongoDB에서는 `@Document` 클래스가 사실상의 스키마 역할을 합니다.
- **중첩 도큐먼트 vs. 정규화**: RDBMS처럼 3정규형을 지향하는 대신, 읽기 패턴에 맞춰 중첩 필드를 배치하거나 `embedded document`로 모델링합니다. 트랜잭션 범위를 줄이고 쿼리를 단순화하는 것이 목적입니다.
- **ObjectId vs. Custom ID**: MongoDB 기본 PK는 `ObjectId`이지만, 서비스 도메인 키를 직접 `_id`로 저장하는 설계도 흔합니다. Kotlin에서는 `@Id val id: String` 형태로 매핑하거나 VO를 사용합니다.

## 2. 트랜잭션 및 일관성

- **트랜잭션 기본 범위**: MongoDB는 단일 도큐먼트 쓰기 시 기본적으로 atomic 합니다. 다중 컬렉션 트랜잭션은 Replica Set 이상에서만 가능하며, 성능 오버헤드가 크므로 필요한 시나리오(금융, 재고 이동 등)에서만 사용합니다.
- **Write Concern/Read Concern**: RDBMS의 COMMIT/ROLLBACK 개념과 동일하지 않으며, `WriteConcern.MAJORITY`, `ReadConcern.SNAPSHOT` 등을 선택적으로 지정해야 일관성을 맞출 수 있습니다. Spring Data MongoDB는 `@Transactional` + `MongoTransactionManager`만으로는 Concern을 제어하지 않으므로 템플릿 설정을 추가합니다.
- **ACID vs. BASE**: MongoDB는 기본적으로 BASE 모델에 가깝기 때문에, 스키마 설계/쿼리 설계 단계에서 eventually consistent 상황(Secondary에서 읽기 등)을 고려해야 합니다.

## 3. 쿼리/인덱스 전략

- **JOIN 부재**: `$lookup` 이 있지만 RDBMS의 JOIN처럼 최적화되지 않으므로, JOIN이 필요한 구조를 문서 설계나 응용 계층에서 해결합니다. Kotlin/Spring에서는 Aggregation Pipeline을 빌더로 작성해야 하므로 복잡도가 높습니다.
- **인덱스 설계**: B-Tree 기반이지만 다중 키/텍스트/지오스페이셜 인덱스를 지원합니다. 쿼리 패턴이 명확하지 않으면 인덱스를 남용하기 쉬우므로, `explain()` 으로 쿼리 플랜을 꼭 확인합니다.
- **페이징**: `skip/limit` 는 큰 컬렉션에서 비효율적입니다. `_id` or `createdAt` 기반 커서 페이징(Bookmark 기반)을 설계하는 것이 일반적입니다.

## 4. Kotlin + Spring Data MongoDB 주의사항

- **Immutable 도큐먼트**: Kotlin data class를 사용할 때 `val` 필드만 사용하면 업데이트 시 전체 덮어쓰기 패턴이 기본이 됩니다. 부분 업데이트가 필요하면 `MongoTemplate.update...` 사용, 혹은 `@Builder`/`copy` 로 패치 객체를 생성합니다.
- **Auditing**: Spring Data JPA와 달리 MongoDB도 `@CreatedDate`, `@LastModifiedDate` 를 지원하지만, `@EnableMongoAuditing` 설정을 별도로 해야 하며, ZonedDateTime/Instant 매핑을 명확히 해야 합니다.
- **Reactive vs. Blocking**: Spring Data MongoDB는 리액티브 지원이 있지만 트랜잭션 기능은 리액티브 드라이버와 별도로 관리됩니다. 리액티브와 블로킹을 혼용하지 말고 선택적으로 사용합니다.
- **Object Mapping**: Kotlin의 `sealed class`, `inline class` 등을 MongoConverter가 자동 지원하지 않을 수 있으므로 `@ReadingConverter/@WritingConverter` 를 등록하거나 단순한 자료형으로 매핑합니다.

## 5. BE 개발자가 자주 겪는 착각과 교정

| 착각/오해 | 교정/설명 |
| --- | --- |
| "MongoDB는 스키마가 없으니 아무 구조나 넣어도 된다" | 애플리케이션 코드가 스키마 역할을 하므로, 명확한 모델 정의와 마이그레이션 절차가 필요합니다. 스키마 버전을 필드에 포함시키는 것도 방법입니다. |
| "Replica Set 없이도 트랜잭션이 된다" | 다중 도큐먼트 트랜잭션은 Replica Set 이상에서만 동작합니다. 단일 노드라도 Replica Set 모드로 구성해야 합니다. |
| "MongoDB는 join이 없으니 성능이 무조건 좋다" | 중첩 도큐먼트가 너무 크면 저장·네트워크 비용이 증가합니다. 읽기 패턴을 기반으로 도큐먼트를 설계해야 최적 성능을 얻습니다. |
| "RDBMS 쿼리를 MongoDB 쿼리로 그대로 변환하면 된다" | Aggregation Pipeline 문법과 평가 순서를 이해해야 하며, `$match`를 먼저 배치하는 등 순서를 최적화해야 합니다. |
| "Auto-increment PK가 필요하다" | MongoDB는 단일 노드 락 없이 auto-increment를 제공하지 않으므로, `ObjectId`, UUID, 도메인 키를 사용하거나 별도 counter 컬렉션을 트랜잭션으로 관리해야 합니다. |

## 6. 추가 학습 추천

- **MongoDB 공식 문서**: Data Modeling, Schema Design, Aggregation Pipeline/Transaction 섹션
- **Spring Data MongoDB 레퍼런스**: `MongoTemplate`, `ReactiveMongoTemplate`, `MappingMongoConverter`
- **MongoDB Atlas 실습**: Performance Advisor, Index Analyzer, Profiler 사용법
- **Testcontainers + MongoDB**: 통합 테스트 환경 자동화 및 Replica Set 테스트 방법
- **Monitoring**: `mongostat`, `mongotop`, Micrometer Mongo metric 바인더 활용법

이 문서를 바탕으로 RDBMS와 MongoDB의 차이를 이해하고, Kotlin + Spring Data에서 안전하게 사용할 수 있도록 가이드를 유지보수하세요.
