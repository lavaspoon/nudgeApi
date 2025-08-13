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

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeptNudgeStats {
        private Integer deptIdx;
        private String deptName;
        private Integer totalMembers;
        private Integer totalCount;
        private Integer totalNudgeCount;
        private Integer totalSuccessCount;
        private List<UserNudgeStats> userStats; // 부서별 사용자 통계 추가
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserNudgeStats {
        private String userId;
        private String userName;
        private String mbPositionName;
        private Integer totalCount; // 이달 타겟 건수
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
        private String userId;
        private String userName;
        private String deptName;
        private Integer totalCount;
        private Integer totalNudgeCount;
        private Integer totalSuccessCount;
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
        private Integer deptIdx;
        private String deptName;
        private List<MonthlyNudgeStats> monthlyStats; // 월별 통계
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
}
