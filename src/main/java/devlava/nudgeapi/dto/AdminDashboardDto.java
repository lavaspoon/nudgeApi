package devlava.nudgeapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardDto {

    private List<DeptNudgeStats> deptStats; // 부서의 통계와 구성원의 넛지건수
    private RankingStats rankings; // 4가지 카테고리별 상위 5위
    private List<DeptMonthlyStats> deptMonthlyStats; // 부서별 월별 넛지 건수
    private BigDecimal averageNudgeRate; // 전체 평균 넛지율

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeptNudgeStats {
        private Integer deptIdx; // 부서 인덱스
        private String deptName; // 부서명
        private Integer totalMembers; // 부서 전체 구성원 수
        private Integer totalCount; // 부서 전체 총 건수
        private Integer totalNudgeCount; // 부서 전체 넛지 건수
        private Integer totalSuccessCount; // 부서 전체 성공 건수
        private BigDecimal nudgeSuccessRate; // 넛지 성공률
        private MonthlyComparisonDto monthlyComparison; // 전월 대비 증감 정보
        private List<UserNudgeStats> userStats; // 부서별 사용자 통계 목록
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserNudgeStats {
        private String userId; // 사용자 ID
        private String userName; // 사용자명
        private String mbPositionName; // 사용자 직급명
        private Integer totalCount; // 이달 총 건수
        private Integer nudgeCount; // 이달 넛지 건수
        private Integer gigaCount; // 이달 GIGA 건수
        private Integer crmCount; // 이달 CRM 건수
        private Integer tdsCount; // 이달 TDS 건수
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopUserStats {
        private String userId; // 사용자 ID
        private String userName; // 사용자명
        private String deptName; // 부서명
        private Integer totalCount; // 해당 카테고리 총 건수
        private Integer totalNudgeCount; // 넛지 총 건수
        private Integer totalSuccessCount; // 성공 총 건수
        private BigDecimal nudgeSuccessRate; // 넛지 성공률
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RankingStats {
        private List<TopUserStats> nudgeRanking; // 넛지 건수 상위 5위
        private List<TopUserStats> gigaRanking; // GIGA 건수 상위 5위
        private List<TopUserStats> tdsRanking; // TDS 건수 상위 5위
        private List<TopUserStats> crmRanking; // CRM 건수 상위 5위
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeptMonthlyStats {
        private Integer deptIdx; // 부서 인덱스
        private String deptName; // 부서명
        private List<MonthlyNudgeStats> monthlyStats; // 월별 통계 목록
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyNudgeStats {
        private String month; // yyyyMM 형식
        private Integer totalCount; // 전체 건수
        private Integer nudgeCount; // 넛지 건수
        private Integer successCount; // 성공 건수
        private Integer gigaCount; // GIGA 건수
        private Integer crmCount; // CRM 건수
        private Integer tdsCount; // TDS 건수
        private BigDecimal nudgeRate; // 넛지율 (nudgeCount / totalCount)
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyComparisonDto {
        private Integer prevMonthNudgeCount; // 전월 넛지 건수
        private Integer currentMonthNudgeCount; // 이달 넛지 건수
        private Integer nudgeCountDiff; // 넛지 건수 증감
        private BigDecimal nudgeCountChangeRate; // 넛지 건수 증감률 (%)

        private BigDecimal prevMonthSuccessRate; // 전월 넛지 성공률
        private BigDecimal currentMonthSuccessRate; // 이달 넛지 성공률
        private BigDecimal successRateDiff; // 넛지 성공률 증감
        private BigDecimal successRateChangeRate; // 넛지 성공률 증감률 (%)

        private BigDecimal prevMonthAverageNudgeRate; // 전월 평균 넛지율
        private BigDecimal currentMonthAverageNudgeRate; // 이달 평균 넛지율
        private BigDecimal averageNudgeRateDiff; // 평균 넛지율 증감
        private BigDecimal averageNudgeRateChangeRate; // 평균 넛지율 증감률 (%)

        private String nudgeCountTrend; // 넛지 건수 트렌드 (UP/DOWN/SAME)
        private String successRateTrend; // 성공률 트렌드 (UP/DOWN/SAME)
        private String averageNudgeRateTrend; // 평균 넛지율 트렌드 (UP/DOWN/SAME)
    }
}
