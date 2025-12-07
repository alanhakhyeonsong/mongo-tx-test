# Artillery 부하 테스트 가이드

`loadtest/artillery/order-and-account.yml`은 주문 생성/대량 삽입/포인트 이체 엔드포인트를 동시에 스트레스 테스트하기 위한 스크립트입니다. 아래 절차에 따라 실행 환경을 준비하세요.

## 선행 조건

1. **MongoDB Replica Set 기동**
   - `docker compose up -d` 실행.
   - 최초 1회 `rs.initiate()`를 수행해야 합니다.
     ```bash
     docker compose exec mongodb mongosh --eval 'rs.initiate({_id:"rs0",members:[{_id:0,host:"mongodb:27017"}]})'
     ```

2. **애플리케이션 실행**
   ```bash
   ./gradlew bootRun
   ```

3. **기본 데이터 준비**
   - `inventories` 컬렉션: SKU-0 ~ SKU-9 등 테스트에 필요한 제품을 충분한 수량으로 삽입.
   - `accounts` 컬렉션: `A-1` ~ `A-5`와 같이 포인트 이체에 사용할 계좌/잔액을 저장.

4. **Artillery 설치**
   - 전역 설치 권장:
     ```bash
     npm install -g artillery@2
     ```

## 실행 방법

```bash
artillery run loadtest/artillery/order-and-account.yml
```

### 구성 요소 설명

- **Target**: `http://localhost:8080`
- **Phases**
  - Warmup (60s): 초당 2건으로 주문 API 예열
  - Steady (90s): 초당 5 → 15건까지 램프업하며 주문 생성
  - Bulk burst (60s): 대량 삽입 API 호출로 Mongo 세션 부하 확인
  - Transfer stress (60s): 계좌 이체 API를 2 → 8건/초로 증가
- **Scenarios**
  - `place-order`: SKU/수량을 랜덤 생성해 `/api/v1/orders` 호출 (가중치 6)
  - `bulk-insert`: `batchSize=500`, `chunkSize=100` 으로 `/api/v1/orders/bulk` 호출 (가중치 2)
  - `point-transfer`: 계좌 ID/금액을 랜덤 생성해 `/api/v1/accounts/transfer` 호출 (가중치 4)

### 결과 확인

- 기본 출력은 콘솔에 요약됩니다. 상세 리포트는 `--output report.json` 플래그로 저장 후 `artillery report report.json` 으로 HTML 생성 가능.
- 트랜잭션 실패(HTTP 4xx/5xx) 비율이 높은 경우 MongoDB 로그(`docker compose logs mongodb`)와 애플리케이션 로그를 함께 점검하세요.

## 커스터마이징 팁

- **부하 패턴 변경**: `phases`의 `arrivalRate`/`rampTo`를 조정해 트래픽 프로파일을 바꿀 수 있습니다.
- **데이터 다양화**: `functions` 섹션 내 JavaScript를 수정해 SKU 혹은 계좌 범위를 늘릴 수 있습니다.
- **환경 분리**: 타겟 URL을 스테이징/프로덕션 게이트웨이에 맞게 교체하고 인증 헤더 등을 `headers`에 추가하세요.
