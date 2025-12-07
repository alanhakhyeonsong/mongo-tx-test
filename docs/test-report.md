# 테스트 실행 결과 (2025-12-07)

## 1. Gradle 전체 테스트

- **커맨드**: `./gradlew test`
- **결과**: 성공 (BUILD SUCCESSFUL)
- **비고**:
  - 도메인 단위 테스트, Bulk 주문 시나리오 테스트, Mongo 트랜잭션 통합 테스트(도커 사용 시 자동 실행)가 포함됩니다.
  - 실행 환경에 Docker가 없으면 `MongoTransactionIntegrationTest`는 자동으로 건너뜁니다.
  - Gradle 9.0 호환성을 위한 경고가 존재하므로 `--warning-mode all`로 확인 권장.

## 2. Artillery 부하 테스트

- **커맨드**: `artillery run loadtest/artillery/order-and-account.yml`
- **결과**: 성공(HTTP 200만 응답) – 테스트가 타임아웃으로 중단되었지만 실패(vusers.failed) 없이 종료됨
- **요약 지표(발췌)**:
  - Warmup 구간(60s): 초당 2건, `http.codes.200` = 12, 평균 응답 37.6ms (p95 19.9ms)
  - Steady 구간: 초당 5 → 15건, `http.codes.200` = 20 ~ 30, 평균 응답 45 ~ 68ms, p95 242 ~ 308ms
  - Bulk burst / Transfer stress: 초당 3 ~ 8건, `vusers.failed = 0`, `http.codes.200` = 30, 평균 응답 45 ~ 49ms, p95 약 262ms
  - 전체 실행 동안 `errors.*` 없음, `vusers.failed = 0`
- **비고**:
  - 스크립트가 총 4개 phase(270초)를 모두 마치기 전에 타임아웃으로 중단되었으므로 더 긴 관측이 필요하면 `artillery run ... --output report.json` 후 `artillery report` 로 HTML 리포트를 생성하는 것을 권장합니다.
  - 모든 응답이 200이므로 샘플 데이터(`mongo-seed`)의 효과를 확인했습니다.

이 문서는 향후 반복 테스트 시 결과를 누적 기록하는 용도로 사용할 수 있습니다.
