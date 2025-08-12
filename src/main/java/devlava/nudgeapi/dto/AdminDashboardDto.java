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

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeptNudgeStats {
        private Integer deptIdx;
        private String deptName;
        private Integer totalMembers;
        private Integer totalNudgeCount;
        private Integer totalSuccessCount;
        private BigDecimal nudgeRate;
        private Integer workingDays;
        private Integer avgNudgePerDay;
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
        private BigDecimal nudgeRate; // 이달 넛지율
        private Integer nudgeCount; // 이달 넛지 건수
        private Integer gigaCount; // 이달 GIGA 건수
        private Integer crmCount; // 이달 CRM 건수
        private Integer tdsCount; // 이달 TDS 건수
        private BigDecimal prevDayNudgeRate; // 전일 넛지율
        private Integer prevDayNudgeCount; // 전일 넛지 건수
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopUserStats {
        private String userId;
        private String userName;
        private String deptName;
        private Integer totalNudgeCount;
        private Integer totalSuccessCount;
        private BigDecimal nudgeRate;
        private Integer totalPoints;
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

    private List<DeptNudgeStats> deptStats;
    private RankingStats rankings; // 4가지 카테고리별 상위 5위
    private String userComCode;
    private String userDeptName;
}
