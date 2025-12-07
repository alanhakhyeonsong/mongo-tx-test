# 테스트용 MongoDB Document 구조

테스트 및 Artillery 부하 시나리오에서 사용하는 도큐먼트 스키마를 정리합니다. 샘플 데이터는 `scripts/seed/mongo-seed.js` 로 초기화할 수 있습니다.

## 1. `accounts` 컬렉션

| 필드 | 타입 | 설명 |
| --- | --- | --- |
| `_id` | `String` (예: `A-1`) | AccountId 값. 비즈니스 키를 그대로 사용합니다. |
| `ownerName` | `String` | 계좌 보유자 이름. |
| `pointBalance` | `Long` | 현재 포인트 잔액. 음수 불가. |
| `updatedAt` | `ISODate` | 마지막 갱신 시각. Spring Data에서 `ZonedDateTime`으로 매핑. |

## 2. `point_ledgers` 컬렉션

| 필드 | 타입 | 설명 |
| --- | --- | --- |
| `_id` | `String` (UUID) | 원장 레코드 식별자. |
| `fromAccountId` | `String` | 송신 계좌 ID. |
| `toAccountId` | `String` | 수신 계좌 ID. |
| `amount` | `Long` | 이동한 포인트. |
| `createdAt` | `ISODate` | 이벤트 발생 시각. |

## 3. `inventories` 컬렉션

| 필드 | 타입 | 설명 |
| --- | --- | --- |
| `_id` | `String` (예: `SKU-0`) | productCode. |
| `quantity` | `Int` | 재고 수량. 0 이상. |

## 4. `orders` 컬렉션

| 필드 | 타입 | 설명 |
| --- | --- | --- |
| `_id` | `String` (UUID 또는 clientOrderId) | OrderId. |
| `status` | `String` (`CREATED`, `FAILED` 등) | 주문 상태. Domain Enum과 1:1 매핑. |
| `items` | `Array` of `OrderItemDocument` | 주문 상세. |
| `items[].productCode` | `String` | SKU 코드. |
| `items[].quantity` | `Int` | 주문 수량. 1 이상. |
| `createdAt` | `ISODate` | 주문 생성 시각. |

## 5. 시드 스크립트(`scripts/seed/mongo-seed.js`) 요약

- `accounts`: `A-1` ~ `A-5` 계좌를 각각 5,000~15,000 포인트로 초기화합니다.
- `inventories`: `SKU-0` ~ `SKU-9` 를 각 500개 수량으로 생성합니다.
- `point_ledgers`, `orders`: 시드 단계에서는 비워져 있으며 테스트 실행 중 생성됩니다.

필요에 따라 도큐먼트 구조가 변경되면 본 문서를 업데이트하고, 시드 스크립트와 도메인 모델(`@Document`) 정의를 함께 수정해 주세요.
