# Docs Overview

프로젝트 문서 경로(`docs/`)에 포함된 주요 가이드를 빠르게 찾을 수 있도록 목차를 제공합니다.

## 목차

1. [diagrams.md](./diagrams.md)
   - 헥사고날 아키텍처 샘플의 클래스 다이어그램과 주요 시나리오(주문 생성/대량 삽입/포인트 이체) 시퀀스 다이어그램을 확인할 수 있습니다.

2. [rest-api.md](./rest-api.md)
   - Web Adapter로 노출되는 REST API(`POST /api/v1/orders`, `/bulk`, `/accounts/transfer`) 요청/응답 스펙과 예시를 정리했습니다.

3. [load-test.md](./load-test.md)
   - Artillery 부하 테스트 실행 방법, Replica Set 준비 절차, 샘플 데이터 시드(`mongo-seed.js`)가 포함되어 있습니다.

4. [tests.md](./tests.md)
   - 현재 구성된 Kotest/MockK 단위 테스트 및 Testcontainers 통합 테스트 클래스 목록, 확장 아이디어를 제공합니다.

5. [test-report.md](./test-report.md)
   - 최근 `./gradlew test`, `artillery run ...` 실행 결과와 주요 메트릭을 기록합니다. 반복 테스트 시 업데이트 용도로 사용합니다.

6. [mongodb-vs-rdbms.md](./mongodb-vs-rdbms.md)
   - MongoDB와 RDBMS의 구조/철학 비교, Kotlin + Spring Data MongoDB 운영 시 주의점, 흔한 오해, 추가 학습 항목을 종합한 가이드입니다.

## 추가 노트

- `scripts/seed/mongo-seed.js` 관련 설명은 README와 `load-test.md`에 포함되어 있으므로, 데이터 세팅 전에 해당 문서를 참고하세요.
- 새로운 문서를 추가하면 `docs/README.md`에 항목을 덧붙여 팀 구성원들이 쉽게 찾을 수 있도록 관리해 주세요.
