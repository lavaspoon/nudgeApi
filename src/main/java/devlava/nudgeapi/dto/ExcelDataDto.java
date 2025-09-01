package devlava.nudgeapi.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;
import java.util.Map;

public class ExcelDataDto {

    // 부서별 통합 통계
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DeptStatistics {
        private Integer deptIdx;
        private String deptName;
        private Long totalCount;
        private Long nudgeCount;
        private Long positiveCount;
        private Long gigaCount;
        private Long crmCount;
        private Long tdsCount;
        private Double nudgeRate;
        private Double positiveRate;
        private Double gigaRate;
        private Double crmRate;
        private Double tdsRate;
    }

    // 구성원별 통합 통계
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MemberStatistics {
        private Integer deptIdx;
        private String deptName;
        private String userId;
        private String mbName;
        private Long totalCount;
        private Long nudgeCount;
        private Long positiveCount;
        private Long gigaCount;
        private Long crmCount;
        private Long tdsCount;
        private Double nudgeRate;
        private Double positiveRate;
        private Double gigaRate;
        private Double crmRate;
        private Double tdsRate;
    }

    // 전체 엑셀 데이터
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ExcelStatisticsData {
        private String targetMonth;
        private List<DeptStatistics> deptStatistics;
        private List<MemberStatistics> memberStatistics;
    }
}
