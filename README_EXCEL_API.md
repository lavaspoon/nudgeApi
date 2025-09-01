# 엑셀 데이터 추출 API 문서

## 개요
선택한 월 기준으로 통계 데이터를 추출하여 엑셀 파일 생성에 필요한 데이터를 제공하는 API입니다.

## 기본 URL
```
/api/excel
```

## API 엔드포인트

### 통계 데이터 조회
선택한 월의 모든 통계 데이터를 한 번에 조회합니다.

**URL:** `GET /api/excel/statistics/{targetMonth}`

**Path Parameter:**
- `targetMonth`: 조회할 월 (YYYY-MM 형식, 예: "2024-01")

**Response:**
```json
{
  "targetMonth": "2024-01",
  "deptStatistics": [
    {
      "deptIdx": 4,
      "deptName": "영업1팀",
      "totalCount": 150,
      "nudgeCount": 120,
      "positiveCount": 90,
      "gigaCount": 40,
      "crmCount": 50,
      "tdsCount": 30,
      "nudgeRate": 80.0,
      "positiveRate": 75.0,
      "gigaRate": 33.33,
      "crmRate": 41.67,
      "tdsRate": 25.0
    }
  ],
  "memberStatistics": [
    {
      "deptIdx": 14,
      "deptName": "로열 4실",
      "userId": "royal4_chief01",
      "mbName": "배로열4실장A",
      "totalCount": 0,
      "nudgeCount": 0,
      "positiveCount": 0,
      "gigaCount": 0,
      "crmCount": 0,
      "tdsCount": 1,
      "nudgeRate": 0.0,
      "positiveRate": 0.0,
      "gigaRate": 0.0,
      "crmRate": 0.0,
      "tdsRate": 100.0
    }
  ]
}
```

## 데이터 설명

### 부서별 통계
- **DeptConfig의 adminDashboardTargetDepts**에 정의된 부서들을 대상으로 계산
- 각 부서의 구성원들의 데이터를 집계하여 부서별 통계 생성

### 구성원별 통계
- **DeptConfig의 adminDashboardTargetDepts**에 정의된 부서의 각 구성원별 통계
- 개별 구성원의 성과를 세부적으로 분석 가능

### 계산 방식
1. **넛지율**: `(nudgeYn='Y' 건수 / 전체 건수) * 100`
2. **긍정율**: `(customerConsentYn='Y' 건수 / nudgeYn='Y' 건수) * 100`
3. **마케팅 유형별 비율**: `(각 마케팅 유형 건수 / nudgeYn='Y' 건수) * 100`

### 마케팅 유형
- **GIGA**: GIGA 관련 마케팅
- **CRM**: CRM 관련 마케팅  
- **TDS**: TDS 관련 마케팅

## 에러 응답

### 400 Bad Request
- 잘못된 월 형식 (YYYY-MM 형식이 아닌 경우)

### 500 Internal Server Error
- 서버 내부 오류

## 사용 예시

### 통계 데이터 조회
```bash
curl -X GET "http://localhost:8080/api/excel/statistics/2024-01"
```
