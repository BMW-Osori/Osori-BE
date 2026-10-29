# 위험구역 데이터 적재

위험구역 조회, 지도 조회, 안전 요약 API가 사용할 데이터를 JSON 파일에서 적재합니다. 이 기능은 `risk-zone-import.file` 옵션을 지정한 실행에서만 동작하며 공개 API를 추가하지 않습니다.

## 파일 형식

최상위 값은 JSON 배열입니다. 각 항목의 필드는 모두 필수입니다.

```json
[
  {
    "sourceKey": "sample-crosswalk-001",
    "name": "테스트 횡단보도",
    "latitude": 37.5665,
    "longitude": 126.978,
    "radius": 100,
    "riskScore": 87.5,
    "riskLevel": "HIGH",
    "accidentCount": 3,
    "modelVersion": "sample-v1"
  }
]
```

위 예시는 형식 확인용 가상 데이터이며 실제 보행 위험을 나타내지 않습니다. `sourceKey`는 원천 데이터에서 변하지 않는 고유 ID를 사용하세요. 같은 키를 다시 적재하면 기존 위험구역을 갱신하고, 내용이 같으면 변경하지 않습니다.

- `latitude`: -90~90, `longitude`: -180~180
- `radius`: 1m 이상, `riskScore`: 0 이상, `accidentCount`: 0 이상
- `riskLevel`: `LOW`, `MEDIUM`, `HIGH`
- 문자열 길이: `sourceKey`와 `name`은 100자 이하, `modelVersion`은 50자 이하

## 실행

DB 연결과 스키마가 준비된 환경에서 절대 경로로 파일을 지정합니다.

```bash
./gradlew bootRun --args='--spring.main.web-application-type=none --risk-zone-import.file=/absolute/path/risk-zones.json'
```

적재 결과는 로그에 생성·갱신·변경 없음·실패 건수와 실패 행 번호로 표시됩니다. 잘못된 행은 건너뛰고 유효한 행을 저장합니다. JSON 문법 오류나 파일 읽기 실패는 적재를 중단합니다. DB 연결 및 초기 마이그레이션 설정은 별도 이슈에서 진행합니다.
