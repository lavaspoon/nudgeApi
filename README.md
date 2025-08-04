# ng API - 다중 데이터베이스 설정

이 프로젝트는 JPA를 사용하여 2개의 분리된 데이터베이스 서버에 연결하는 Spring Boot 애플리케이션입니다.

## 데이터베이스 구조

- **유저 데이터베이스 (user_db)**: 사용자 정보 저장
- **데이터 데이터베이스 (data_db)**: 데이터, 포인트 내역, 성공 사례 등 저장

## 설정 방법

### 1. 데이터베이스 생성

```sql
-- 유저 데이터베이스
CREATE DATABASE user_db;
USE user_db;

-- 데이터 데이터베이스  
CREATE DATABASE data_db;
USE data_db;
```

### 2. application.yml 설정

현재 설정된 데이터베이스 연결 정보를 실제 환경에 맞게 수정하세요:

```yaml
spring:
  datasource:
    user:
      jdbc-url: jdbc:mysql://localhost:3306/user_db
      username: your_username
      password: your_password
    data:
      jdbc-url: jdbc:mysql://localhost:3306/data_db
      username: your_username
      password: your_password
```

### 3. 프로젝트 실행

```bash
./gradlew bootRun
```

## API 엔드포인트

### 유저 관련 API

- `POST /api/users` - 유저 생성
- `GET /api/users/{id}` - 유저 조회
- `GET /api/users/email/{email}` - 이메일로 유저 조회
- `GET /api/users` - 모든 유저 조회
- `GET /api/users/{id}/with-data` - 유저와 데이터 함께 조회
- `GET /api/users/{id}/with-data/{dataType}` - 유저와 특정 타입 데이터 조회
- `DELETE /api/users/{id}` - 유저 삭제
- `DELETE /api/users/{id}/with-data` - 유저와 데이터 함께 삭제

### 유저 데이터 관련 API

- `POST /api/user-data/{userId}` - 유저 데이터 생성
- `GET /api/user-data/{id}` - 데이터 조회
- `GET /api/user-data/user/{userId}` - 유저의 모든 데이터 조회
- `GET /api/user-data/user/{userId}/type/{dataType}` - 유저의 특정 타입 데이터 조회
- `GET /api/user-data` - 모든 데이터 조회
- `DELETE /api/user-data/{id}` - 데이터 삭제

### 대시보드 API

- `GET /api/dashboard/{userId}` - 대시보드 데이터 조회 (월별/일별 통계, 포인트 정보, 성공 사례 등)

### 포인트 시스템 API

- `GET /api/points/{userId}/info` - **통합 포인트 정보 조회** (현재 포인트, 등급, 내역 모두 포함)
- `GET /api/points/{userId}/balance` - 현재 포인트 잔액 조회
- `GET /api/points/{userId}/history` - 포인트 내역 조회
- `GET /api/points/{userId}/history/{pointType}` - 포인트 타입별 내역 조회 (EARN/USE)
- `POST /api/points/{userId}/earn` - 포인트 적립
- `POST /api/points/{userId}/use` - 포인트 사용
- `POST /api/points/{userId}/earn/ng-success` -  성공 시 포인트 적립
- `POST /api/points/{userId}/earn/customer-satisfaction` - 고객 만족도 우수 시 포인트 적립
- `POST /api/points/{userId}/earn/daily-achievement` - 일일 성과 달성 시 포인트 적립
- `POST /api/points/{userId}/earn/weekly-first` - 주간 성과 1위 시 포인트 적립
- `POST /api/points/{userId}/earn/monthly-excellence` - 월간 우수원 시 포인트 적립
- `GET /api/points/{userId}/stats` - 포인트 통계 조회

### 스케줄러 API

- `POST /api/scheduler/ng-points/{date}` - 특정 날짜의  포인트 적립 수동 실행
- `POST /api/scheduler/ng-points/yesterday` - 어제 날짜의  포인트 적립 수동 실행
- `GET /api/scheduler/ng-count/{userId}/{date}` - 특정 사용자의 특정 날짜  성공 건수 조회
- `GET /api/scheduler/userIds` - 전체 구성원 목록 조회
- `GET /api/scheduler/status` - 스케줄러 상태 확인

## 데이터베이스 테이블 구조

### 유저 데이터베이스 (user_db)
- `users` - 사용자 정보

### 데이터 데이터베이스 (data_db)
- `ng_consultations` -   데이터
- `point_history` - 포인트 적립/사용 내역
- `success_stories` - 동료 성공 사례
- `user_data` - 기타 유저 데이터

## 주요 기능

### 1. 대시보드 기능
- 월별/일별  성공률 통계
타입별 성공 건수 ( 전환,  전환,  전환)
- 동료 성공 사례 조회
- 포인트 시스템 및 등급 관리

### 2. 포인트 시스템
-  성공 시 자동 포인트 적립
- 등급별 혜택 시스템 (브론즈, 실버, 골드, 플래티넘)
- 포인트 적립/사용 내역 관리
- 팀 내 순위 시스템
- **통합 포인트 정보 API**: 하나의 API 호출로 현재 포인트, 등급, 적립/사용 내역을 모두 조회

### 3. 스케줄러 시스템
- **자동 포인트 적립**: 매일 새벽 2시에 어제  성공 건수에 따라 건당 50포인트씩 자동 적립
- **수동 실행**: 특정 날짜의 포인트 적립을 수동으로 실행 가능
- **실시간 모니터링**: 스케줄러 상태 및 실행 결과 확인

## 사용 예시

### 대시보드 데이터 조회
```bash
curl http://localhost:8080/api/dashboard/KIM001
```

### 통합 포인트 정보 조회
```bash
curl http://localhost:8080/api/points/KIM001/info
```

응답 예시:
```json
{
  "currentPoints": 2450,
  "currentGrade": "실버",
  "nextGrade": "골드",
  "gradeProgress": 58,
  "teamRank": 3,
  "weeklyEarned": 300,
  "totalEarned": 3000,
  "totalUsed": 550,
  "earnHistory": [
    {
      "id": 1,
      "pointType": "EARN",
      "pointAmount": 150,
      "pointReason": "성공 보너스",
      "pointDescription": "포인트를 적립받았습니다.",
      "balanceAfter": 2450,
      "createdAt": "2025-01-29 14:30:00"
    }
  ],
  "useHistory": [
    {
      "id": 2,
      "pointType": "USE",
      "pointAmount": 500,
      "pointReason": "카페 기프티콘",
      "pointDescription": "카페 기프티콘으로 교환",
      "balanceAfter": 1950,
      "createdAt": "2025-01-28 10:15:00"
    }
  ]
}
```

### 스케줄러 수동 실행
```bash
# 특정 날짜의 포인트 적립 실행
curl -X POST http://localhost:8080/api/scheduler/ng-points/2025-01-29

# 어제 날짜의 포인트 적립 실행
curl -X POST http://localhost:8080/api/scheduler/ng-points/yesterday
```

### 성공 건수 조회
```bash
curl http://localhost:8080/api/scheduler/ng-count/KIM001/2025-01-29
```

응답 예시:
```json
{
  "id": "KIM001",
  "date": "2025-01-29",
  "ngSuccessCount": 3,
  "estimatedPoints": 150
}
```

### 스케줄러 상태 확인
```bash
curl http://localhost:8080/api/scheduler/status
```

응답 예시:
```json
{
  "schedulerEnabled": true,
  "nextExecution": "매일 새벽 2시",
  "pointsPerng": 50,
  "description": "어제 성공 건수에 따라 건당 50포인트씩 적립"
}
```

### 포인트 적립
```bash
curl -X POST http://localhost:8080/api/points/KIM001/earn \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 150,
    "reason": " 성공 보너스",
    "description": "고객이  제안에 동의하여 포인트를 적립받았습니다."
  }'
```

### 포인트 잔액 조회
```bash
curl http://localhost:8080/api/points/KIM001/balance
```

## 주요 특징

1. **분리된 데이터베이스**: 유저 정보와 비즈니스 데이터가 물리적으로 분리되어 있습니다.
2. **트랜잭션 관리**: 각 데이터베이스별로 독립적인 트랜잭션 관리가 가능합니다.
3. **통합 서비스**: 두 데이터베이스의 데이터를 함께 조회할 수 있는 통합 서비스가 제공됩니다.
4. **포인트 시스템**:  성공에 따른 포인트 적립 및 등급 시스템을 제공합니다.
5. **통합 포인트 API**: 하나의 API 호출로 포인트 관련 모든 정보를 조회할 수 있습니다.
6. **실시간 통계**: 월별/일별 통계를 실시간으로 계산하여 제공합니다.
7. **자동 스케줄러**: 매일 새벽 2시에 어제  성공 건수에 따라 자동으로 포인트를 적립합니다.

## 스케줄러 설정

### 자동 실행 스케줄
- **실행 시간**: 매일 새벽 2시
- **처리 대상**: 어제 성공 건수
- **적립 포인트**: 건당 50포인트
- **처리 조건**: `ngYn = 'Y'` AND `customerConsentYn = 'Y'`

### 수동 실행
- 특정 날짜의 포인트 적립을 수동으로 실행할 수 있습니다.
- 스케줄러 상태 및 실행 결과를 실시간으로 확인할 수 있습니다.

## 주의사항

1. 데이터베이스 연결 정보는 보안을 위해 환경 변수나 별도 설정 파일로 관리하는 것을 권장합니다.
2. 프로덕션 환경에서는 적절한 인증 및 권한 설정이 필요합니다.
3. 트랜잭션 경계를 명확히 하여 데이터 일관성을 유지해야 합니다.
4. 포인트 시스템의 경우 중복 적립을 방지하는 로직이 필요할 수 있습니다.
5. 스케줄러는 서버 시간을 기준으로 실행되므로 서버 시간 설정을 확인하세요. 