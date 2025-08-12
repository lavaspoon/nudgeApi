# 관리자 대시보드 API

## 개요
관리자 권한에 따른 넛지 통계 데이터를 제공하는 API입니다.

## 권한별 접근 제어

### comCode 45 이상 (고급 관리자)
- parent 부서가 2 또는 3인 부서들의 통계 데이터 조회
- 부서별 그룹화된 통계 제공

### comCode 35 이상 (중급 관리자)
- 자신이 속한 실의 구성원만 조회
- 실 단위 통계 제공

### comCode 35 미만
- 관리자 권한 없음 (접근 거부)

## API 엔드포인트

### 관리자 대시보드 조회
```
GET /api/admin/dashboard/{userId}
```

#### 요청 파라미터
- `userId` (Path Variable): 조회할 관리자의 사용자 ID

#### 응답 예시
```json
{
  "result": true,
  "errorMessage": null,
  "data": {
    "deptStats": [
      {
        "deptIdx": 1,
        "deptName": "영업1팀",
        "totalMembers": 15,
        "totalNudgeCount": 150,
        "totalSuccessCount": 120,
        "nudgeRate": 80.0000,
        "workingDays": 22,
        "avgNudgePerDay": 6,
        "userStats": [
          {
            "userId": "user123",
            "userName": "홍길동",
            "mbPositionName": "팀장",
            "nudgeRate": 85.5000,
            "nudgeCount": 25,
            "gigaCount": 10,
            "crmCount": 8,
            "tdsCount": 7,
            "prevDayNudgeRate": 90.0000,
            "prevDayNudgeCount": 3
          }
        ]
      }
    ],
    "rankings": {
      "nudgeRanking": [
        {
          "userId": "user123",
          "userName": "홍길동",
          "deptName": "영업1팀",
          "totalNudgeCount": 25,
          "totalSuccessCount": 22,
          "nudgeRate": 88.0000,
          "totalPoints": 1500
        }
      ],
      "gigaRanking": [
        {
          "userId": "user456",
          "userName": "김철수",
          "deptName": "영업2팀",
          "totalNudgeCount": 15,
          "totalSuccessCount": 0,
          "nudgeRate": 0.0000,
          "totalPoints": 1200
        }
      ],
      "tdsRanking": [
        {
          "userId": "user789",
          "userName": "박영희",
          "deptName": "영업3팀",
          "totalNudgeCount": 12,
          "totalSuccessCount": 0,
          "nudgeRate": 0.0000,
          "totalPoints": 1000
        }
      ],
      "crmRanking": [
        {
          "userId": "user101",
          "userName": "이민수",
          "deptName": "영업4팀",
          "totalNudgeCount": 18,
          "totalSuccessCount": 0,
          "nudgeRate": 0.0000,
          "totalPoints": 1400
        }
      ]
    },
    "userComCode": "45",
    "userDeptName": "영업본부"
  }
}
```

## 데이터 설명

### DeptNudgeStats (부서별 통계)
- `deptIdx`: 부서 ID
- `deptName`: 부서명
- `totalMembers`: 부서 구성원 수
- `totalNudgeCount`: 이번달 총 넛지 건수
- `totalSuccessCount`: 이번달 넛지 성공 건수
- `nudgeRate`: 넛지 성공률 (%)
- `workingDays`: 이번달 영업일 수
- `avgNudgePerDay`: 일평균 넛지 건수
- `userStats`: 부서별 사용자 상세 통계 목록

### UserNudgeStats (사용자별 상세 통계)
- `userId`: 사용자 ID
- `userName`: 사용자명
- `mbPositionName`: 직급명
- `nudgeRate`: 이달 넛지율 (이달 모든 건수 / 이달 nudgeYn 건수)
- `nudgeCount`: 이달 넛지 건수
- `gigaCount`: 이달 GIGA 건수 (marketingType LIKE 'GIGA%')
- `crmCount`: 이달 CRM 건수 (marketingType LIKE 'CRM%')
- `tdsCount`: 이달 TDS 건수 (marketingType LIKE 'TDS%')
- `prevDayNudgeRate`: 전일 넛지율
- `prevDayNudgeCount`: 전일 넛지 건수

### RankingStats (순위별 통계)
- `nudgeRanking`: 넛지 건수 상위 5위
- `gigaRanking`: GIGA 건수 상위 5위
- `tdsRanking`: TDS 건수 상위 5위
- `crmRanking`: CRM 건수 상위 5위

### TopUserStats (상위 사용자 통계)
- `userId`: 사용자 ID
- `userName`: 사용자명
- `deptName`: 소속 부서
- `totalNudgeCount`: 해당 카테고리 건수
- `totalSuccessCount`: 넛지 성공 건수 (넛지 카테고리만 의미있음)
- `nudgeRate`: 넛지 성공률 (%) (넛지 카테고리만 의미있음)
- `totalPoints`: 이번달 총 포인트

## 성능 최적화

### N+1 문제 방지
- 부서별 사용자 목록을 한 번에 조회
- 사용자별 넛지 통계를 IN 절로 한 번에 조회
- 사용자별 상세 통계를 IN 절로 한 번에 조회
- 전일 통계를 IN 절로 한 번에 조회
- 포인트 정보를 개별 조회하지 않고 통계 쿼리로 처리

### 쿼리 최적화
- 재귀 쿼리로 부서 계층 구조 조회
- 인덱스를 활용한 효율적인 데이터 접근
- 불필요한 조인 최소화
- marketingType LIKE 검색으로 GIGA, CRM, TDS 분류

## 에러 처리

### 권한 부족
```json
{
  "result": false,
  "errorMessage": "관리자 권한이 없습니다.",
  "data": null
}
```

### 사용자 없음
```json
{
  "result": false,
  "errorMessage": "사용자를 찾을 수 없습니다: user123",
  "data": null
}
```

### 서버 오류
```json
{
  "result": false,
  "errorMessage": "서버 오류가 발생했습니다.",
  "data": null
}
```

## 계산 방식

### 넛지율 계산
- **이달 넛지율**: (이달 nudgeYn='Y' 건수 / 이달 전체 건수) × 100
- **전일 넛지율**: (전일 nudgeYn='Y' 건수 / 전일 전체 건수) × 100

### 마케팅 타입별 건수
- **GIGA 건수**: marketingType LIKE 'GIGA%' 조건으로 조회
- **CRM 건수**: marketingType LIKE 'CRM%' 조건으로 조회  
- **TDS 건수**: marketingType LIKE 'TDS%' 조건으로 조회

### 순위별 통계
- **넛지 건수 상위 5위**: nudgeYn='Y' 조건으로 건수 기준 정렬
- **GIGA 건수 상위 5위**: marketingType LIKE 'GIGA%' 조건으로 건수 기준 정렬
- **TDS 건수 상위 5위**: marketingType LIKE 'TDS%' 조건으로 건수 기준 정렬
- **CRM 건수 상위 5위**: marketingType LIKE 'CRM%' 조건으로 건수 기준 정렬
