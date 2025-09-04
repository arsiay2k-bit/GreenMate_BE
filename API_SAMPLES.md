# GreenMate API 샘플

## 1. 경로 추천 API

### POST /api/routes/search
출발지와 목적지를 입력하여 3가지 경로 옵션을 받습니다.

**요청 예시:**
```bash
curl -X POST http://localhost:8080/api/routes/search \
  -H "Content-Type: application/json" \
  -d '{
    "startLatitude": 37.5663,
    "startLongitude": 126.9997,
    "startAddress": "서울시청",
    "endLatitude": 37.5636,
    "endLongitude": 126.9866,
    "endAddress": "명동"
  }'
```

**응답 예시:**
```json
{
  "routes": [
    {
      "routeType": "LESS_WALK",
      "distanceKm": 0.35,
      "durationMinutes": 7,
      "estimatedSteps": 437,
      "startLocation": {
        "latitude": 37.5663,
        "longitude": 126.9997,
        "address": "서울시청",
        "name": null
      },
      "endLocation": {
        "latitude": 37.5636,
        "longitude": 126.9866,
        "address": "명동",
        "name": null
      },
      "waypoints": null,
      "polyline": "mock_polyline_37.566300_126.999700_to_37.563600_126.986600"
    },
    {
      "routeType": "RECOMMENDED",
      "distanceKm": 0.42,
      "durationMinutes": 8,
      "estimatedSteps": 525,
      "startLocation": {
        "latitude": 37.5663,
        "longitude": 126.9997,
        "address": "서울시청",
        "name": null
      },
      "endLocation": {
        "latitude": 37.5636,
        "longitude": 126.9866,
        "address": "명동",
        "name": null
      },
      "waypoints": null,
      "polyline": "mock_polyline_37.566300_126.999700_to_37.563600_126.986600"
    },
    {
      "routeType": "MORE_WALK",
      "distanceKm": 0.52,
      "durationMinutes": 10,
      "estimatedSteps": 650,
      "startLocation": {
        "latitude": 37.5663,
        "longitude": 126.9997,
        "address": "서울시청",
        "name": null
      },
      "endLocation": {
        "latitude": 37.5636,
        "longitude": 126.9866,
        "address": "명동",
        "name": null
      },
      "waypoints": null,
      "polyline": "mock_polyline_37.566300_126.999700_to_37.563600_126.986600"
    }
  ]
}
```

## 2. 위치 검색 API

### GET /api/locations/search
키워드로 위치를 검색합니다.

**요청 예시:**
```bash
curl "http://localhost:8080/api/locations/search?query=강남"
```

**응답 예시:**
```json
[
  {
    "latitude": 37.4981,
    "longitude": 127.0276,
    "address": "서울특별시 강남구 강남대로 390",
    "name": "강남역"
  }
]
```

### GET /api/locations/autocomplete
자동완성 검색 (개수 제한 가능)

**요청 예시:**
```bash
curl "http://localhost:8080/api/locations/autocomplete?query=홍&limit=3"
```

**응답 예시:**
```json
[
  {
    "latitude": 37.5563,
    "longitude": 126.9244,
    "address": "서울특별시 마포구 양화로 160",
    "name": "홍대입구역"
  }
]
```

## 3. 걸음 기록 API

### POST /api/walk-records
완료된 걸음 기록을 저장합니다.

**요청 예시:**
```bash
curl -X POST http://localhost:8080/api/walk-records \
  -H "Content-Type: application/json" \
  -d '{
    "startLatitude": 37.5663,
    "startLongitude": 126.9997,
    "startAddress": "서울시청",
    "endLatitude": 37.5636,
    "endLongitude": 126.9866,
    "endAddress": "명동",
    "distanceKm": 0.42,
    "durationMinutes": 8,
    "steps": 525,
    "routeType": "RECOMMENDED"
  }'
```

**응답 예시:**
```json
{
  "id": 1,
  "startLatitude": 37.5663,
  "startLongitude": 126.9997,
  "startAddress": "서울시청",
  "endLatitude": 37.5636,
  "endLongitude": 126.9866,
  "endAddress": "명동",
  "distanceKm": 0.42,
  "durationMinutes": 8,
  "steps": 525,
  "routeType": "RECOMMENDED",
  "createdAt": "2024-09-04T17:30:15.123456"
}
```

### GET /api/walk-records
모든 걸음 기록을 조회합니다.

**요청 예시:**
```bash
curl "http://localhost:8080/api/walk-records"
```

**응답 예시:**
```json
[
  {
    "id": 1,
    "startLatitude": 37.5663,
    "startLongitude": 126.9997,
    "startAddress": "서울시청",
    "endLatitude": 37.5636,
    "endLongitude": 126.9866,
    "endAddress": "명동",
    "distanceKm": 0.42,
    "durationMinutes": 8,
    "steps": 525,
    "routeType": "RECOMMENDED",
    "createdAt": "2024-09-04T17:30:15.123456"
  }
]
```

### GET /api/walk-records/{id}
특정 걸음 기록을 조회합니다.

**요청 예시:**
```bash
curl "http://localhost:8080/api/walk-records/1"
```

**응답 예시:**
```json
{
  "id": 1,
  "startLatitude": 37.5663,
  "startLongitude": 126.9997,
  "startAddress": "서울시청",
  "endLatitude": 37.5636,
  "endLongitude": 126.9866,
  "endAddress": "명동",
  "distanceKm": 0.42,
  "durationMinutes": 8,
  "steps": 525,
  "routeType": "RECOMMENDED",
  "createdAt": "2024-09-04T17:30:15.123456"
}
```

### GET /api/walk-records/date-range
기간별 걸음 기록을 조회합니다.

**요청 예시:**
```bash
curl "http://localhost:8080/api/walk-records/date-range?startDate=2024-09-01T00:00:00&endDate=2024-09-30T23:59:59"
```

**응답 예시:**
```json
[
  {
    "id": 1,
    "startLatitude": 37.5663,
    "startLongitude": 126.9997,
    "startAddress": "서울시청",
    "endLatitude": 37.5636,
    "endLongitude": 126.9866,
    "endAddress": "명동",
    "distanceKm": 0.42,
    "durationMinutes": 8,
    "steps": 525,
    "routeType": "RECOMMENDED",
    "createdAt": "2024-09-04T17:30:15.123456"
  }
]
```

### GET /api/walk-records/statistics
걸음 통계를 조회합니다.

**요청 예시:**
```bash
curl "http://localhost:8080/api/walk-records/statistics"
```

**응답 예시:**
```json
{
  "totalDistance": 2.15,
  "totalSteps": 2687,
  "totalWalkCount": 5,
  "averageDistance": 0.43
}
```

## 4. 기타 엔드포인트

### GET /actuator/health
애플리케이션 상태를 확인합니다.

**요청 예시:**
```bash
curl "http://localhost:8080/actuator/health"
```

**응답 예시:**
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "H2",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 494384795648,
        "free": 123456789,
        "threshold": 10485760,
        "exists": true
      }
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

### H2 Database Console
개발용 데이터베이스 콘솔에 접속합니다.

**접속 정보:**
- URL: http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:greenmate
- Username: sa
- Password: (공백)

## 테스트용 위치 데이터

서비스에는 다음 서울 주요 위치들이 미리 등록되어 있습니다:
- 강남역, 홍대입구역, 명동, 이태원, 종로3가
- 한강공원, 경복궁, 동대문, 신촌, 건대입구