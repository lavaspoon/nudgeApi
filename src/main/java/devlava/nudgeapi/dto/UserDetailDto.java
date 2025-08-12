package devlava.nudgeapi.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
public class UserDetailDto {
    private String userId;
    private String userName;
    private String mbPositionName;
    private String deptName;
    private List<DailyNudgeData> dailyData;
    private UserSummary summary;

    @Getter
    @Setter
    @Builder
    public static class DailyNudgeData {
        private String date;
        private int totalCount;
        private int nudgeCount;
        private int gigaCount;
        private int crmCount;
        private int tdsCount;
        private BigDecimal nudgeRate;
        private List<NudgeDetail> nudgeDetails;
    }

    @Getter
    @Setter
    @Builder
    public static class NudgeDetail {
        private String consultationDate;
        private String customerInquiry;
        private String marketingType;
        private String marketingMessage;
        private String customerConsentYn;
        private String inappropriateResponseYn;
        private String inappropriateResponseMessage;
    }

    @Getter
    @Setter
    @Builder
    public static class UserSummary {
        private int totalDays;
        private int totalCount;
        private int totalNudgeCount;
        private int totalGigaCount;
        private int totalCrmCount;
        private int totalTdsCount;
        private BigDecimal avgNudgeRate;
        private BigDecimal totalNudgeRate;
    }
}
