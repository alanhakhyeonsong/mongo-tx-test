# REST API 정리

모든 엔드포인트는 JSON 기반이며 베이스 URL은 `http://localhost:8080` 기준입니다.

## 주문 생성

- **Method / Path**: `POST /api/v1/orders`
- **설명**: 재고를 차감하고 주문을 생성합니다. 재고 부족 시 HTTP 400/409 로 매핑하도록 ControllerAdvice 확장 가능.
- **Request Body**
  ```json
  {
    "clientOrderId": "order-123", // 선택
    "items": [
      { "productCode": "SKU-1", "quantity": 2 },
      { "productCode": "SKU-2", "quantity": 1 }
    ]
  }
  ```
- **Response 200**
  ```json
  {
    "orderId": "order-123",
    "status": "CREATED",
    "totalQuantity": 3
  }
  ```

## 대량 주문 삽입

- **Method / Path**: `POST /api/v1/orders/bulk`
- **설명**: `batchSize` 개수만큼 주문을 생성하며 `chunkSize` 단위로 Mongo 트랜잭션을 반복 실행합니다.
- **Request Body**
  ```json
  {
    "batchSize": 5000,
    "chunkSize": 500
  }
  ```
- **Response 200**
  ```json
  {
    "insertedCount": 5000
  }
  ```

## 포인트 이체

- **Method / Path**: `POST /api/v1/accounts/transfer`
- **설명**: 송수신 계좌를 모두 갱신하고 원장에 기록합니다. 계좌가 없거나 포인트 부족 시 예외가 발생합니다.
- **Request Body**
  ```json
  {
    "fromAccountId": "A-1",
    "toAccountId": "A-2",
    "amount": 5000
  }
  ```
- **Response 200**
  ```json
  {
    "fromAccountId": "A-1",
    "toAccountId": "A-2",
    "amount": 5000
  }
  ```

## 공통 고려 사항

- 트랜잭션은 `MongoTransactionManager` 기반이며, Replica Set 환경에서만 정상 동작합니다.
- 대량 요청 시 `chunkSize`와 Mongo 커넥션 풀(`spring.data.mongodb.uri` 옵션) 조정을 권장합니다.
- 오류 응답 매핑은 글로벌 예외 처리기(`@ControllerAdvice`) 추가 시 확장 가능합니다.
