package devlava.nudgeapi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashDto {

    private MonthAnalyze monthAnalyze = new MonthAnalyze(); // 이달 통계
    private CurrentAnalyze currentAnalyze = new CurrentAnalyze(); // 영업일 기준, 하루전 통계
    private List<NudgeResponseDto> monthDatas = new ArrayList<>(); // 이달 전체 데이터
    private List<NudgeResponseDto> curnetDatas = new ArrayList<>(); // 전일 전체 데이터

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthAnalyze {
        private int totalCount; //전체 통화 건수
        private long nudgeCount; //넛지 건수
        private double nudgePercentage; //넛지율
        private long gourp1Count; //넛지 건수 중, group1
        private long gourp2Count; //넛지 건수 중, group2
        private long gourp3Count; //넛지 건수 중, group3
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CurrentAnalyze {
        private int totalCount; //전제 통화 건수
        private long nudgeCount; //넛지 건수
        private double nudgePercentage; //넛지율
        private long gourp1Count; //넛지 건수 중, group1
        private long gourp2Count; //넛지 건수 중, group2
        private long gourp3Count; //넛지 건수 중, group3
        private String group1Growth; //전, 전일 기준 증감
        private String group2Growth; //전, 전일 기준 증감
        private String group3Growth; //전, 전일 기준 증감
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NudgeResponseDto {
        private Long id;
        private String consultationDate; // 상담일
        private String skid; // 사번
        private String customerInquiry; // 고객 문의 사항
        private String nudgeYn; // 넛지 유무
        private String marketingType; // 넛지 유형
        private String marketingMessage; // 넛지 멘트
        private String customerConsentYn; // 고객 넛지 동의 여부
        private String inappropriateResponseYn; // 부정 응대 유무
        private String inappropriateResponseMessage; // 부정 응대 멘트
    }
}
