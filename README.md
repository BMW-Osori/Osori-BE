# Osori-BE

저소음 차량 보행자 촉각 경고 백엔드 (Spring Boot, PostgreSQL). 보행자 디바이스 등록, 차량 접근 이벤트와 RF 신호 샘플 적재, 보행 위험구역·지도 데이터, 안전 요약, 디바이스 감지 통계와 보조 알림 이력 API를 제공합니다.

## API

기본 경로는 `/api/v1` 입니다. springdoc-openapi 를 포함하고 있어 실행 환경에서 Swagger UI(`/swagger-ui/index.html`)로 API 명세를 확인할 수 있습니다.

| 경로 | 설명 |
| --- | --- |
| `/api/v1/devices` | 보행자 수신 디바이스 등록·조회 |
| `/api/v1/proximity-events` | 차량 접근 이벤트 적재·조회 |
| `/api/v1/proximity-events/{eventId}/samples` | 이벤트별 RF 신호 샘플 |
| `/api/v1/risk-zones` | 보행 위험구역 조회 |
| `/api/v1/map` | 지도 표시용 데이터 |
| `/api/v1/safety-summary` | 보행 안전 요약 |
| `/api/v1/statistics/devices` | 디바이스 감지 통계 |
| `/api/v1/notification-histories` | 보조 알림 이력 |

## 실행

JDK 21 과 PostgreSQL 이 필요합니다. DB 연결 정보는 저장소에 두지 않고 실행 환경에서 지정합니다.

```sh
./gradlew test        # 테스트 (H2 사용)
./gradlew bootRun     # 서버 실행
```

저장소 안에 Spring Security 설정 클래스가 없습니다. 시연 환경에서의 허용 설정은 [Osori-Presentation](https://github.com/BMW-Osori/Osori-Presentation) 의 `deploy/backend-patch/` 를 참고하세요. 공개 서비스에는 인증을 따로 설계해야 합니다.

위험구역 데이터 적재는 `docs/risk-zone-import.md` 를 참고하세요.

## 함께 쓰는 저장소

| 저장소 | 역할 |
| --- | --- |
| [Osori-FE](https://github.com/BMW-Osori/Osori-FE) | 앱 (Flutter) |
| [Osori-Presentation](https://github.com/BMW-Osori/Osori-Presentation) | 시연 사이트와 AI 서비스 |
| [Osori-QWEN](https://github.com/BMW-Osori/Osori-QWEN) | 채팅 모델 튜닝 (Qwen2.5-3B) |
