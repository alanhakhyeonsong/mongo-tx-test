# 테스트 실행 결과 (2025-12-07)

## 1. Gradle 전체 테스트

- **커맨드**: `./gradlew test`
- **결과**: 성공 (BUILD SUCCESSFUL)
- **비고**: Kotest 기반 단위 테스트 2종(주문/포인트)을 모두 통과했습니다. Gradle 9.0 호환성을 위한 경고가 존재하므로 추후 `--warning-mode all`로 확인 권장.

## 2. Artillery 부하 테스트

- **커맨드**: `artillery run loadtest/artillery/order-and-account.yml`
- **결과**: 부분 실패 (HTTP 500 및 capture 오류, 실행 타임아웃)
- **요약 지표**:
  - 2xx 응답: 7~73건 구간별 관측, 평균 응답시간 약 250~300ms
  - 5xx 응답: 500번대(존재하지 않는 재고/계좌 등 비즈니스 오류 포함) 다수 발생, 대부분 10ms 이내
  - `errors.Failed capture or match`: Controller 응답 JSON 구조가 Artillery 기본 capture 설정과 맞지 않아 발생
- **추가 조치 필요**:
  1. 테스트 데이터(재고/계좌) 확보 후 HTTP 500 비율을 다시 측정합니다.
  2. Artillery `capture` 섹션을 실제 응답 스키마(`OrderResponse`, `TransferPointResponse`)에 맞게 수정하면 오류 로그를 줄일 수 있습니다.
  3. 보다 긴 지속 시간을 원하면 `--duration` 확장 대신 `phases` 조정 + `--output report.json` 으로 리포트를 저장하세요.

이 문서는 향후 반복 테스트 시 결과를 누적 기록하는 용도로 사용할 수 있습니다.
