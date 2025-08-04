package devlava.nudgeapi.dto;

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
public class DashboardDto {

    private MonthAnalyzeDto monthAnalyze = new MonthAnalyzeDto(); // 이달 통계
    private CurrentAnalyzeDto currentAnalyze = new CurrentAnalyzeDto(); // 영업일 기준, 하루전 통계
    private List<NudgeResponseDto> monthDatas = new ArrayList<>(); // 이달 전체 데이터
    private List<NudgeResponseDto> currentDatas = new ArrayList<>(); // 전일 전체 데이터
    private List<SuccessStoryDto> colleagueSuccessStories = new ArrayList<>(); // 동료 성공 사례
    private PointInfoDto pointInfo = new PointInfoDto(); // 포인트 정보

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthAnalyzeDto {
        private int totalCount; // 전체 통화 건수
        private long nudgeCount; // 넛지 건수
        private double nudgePercentage; // 넛지율
        private long group1Count; // 넛지 건수 중, GIGA 전환
        private long group2Count; // 넛지 건수 중, CRM 전환
        private long group3Count; // 넛지 건수 중, TDS 전환
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CurrentAnalyzeDto {
        private int totalCount; // 전체 통화 건수
        private long nudgeCount; // 넛지 건수
        private double nudgePercentage; // 넛지율
        private long group1Count; // 넛지 건수 중, GIGA 전환
        private long group2Count; // 넛지 건수 중, CRM 전환
        private long group3Count; // 넛지 건수 중, TDS 전환
        private String group1Growth; // 전일 대비 증감
        private String group2Growth; // 전일 대비 증감
        private String group3Growth; // 전일 대비 증감
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

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SuccessStoryDto {
        private Long id;
        private String consultantName; // 상담원 이름
        private String consultantLevel; // 상담원 등급
        private String marketingType; // 마케팅 유형
        private String marketingMessage; // 마케팅 메시지
        private String customerConsentYn; // 고객 동의 여부
        private boolean bookmarked; // 북마크 여부
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PointInfoDto {
        private int currentPoints; // 현재 포인트
        private String currentGrade; // 현재 등급
        private String nextGrade; // 다음 등급
        private int gradeProgress; // 등급 진행률
        private int teamRank; // 팀 내 순위
        private int weeklyEarned; // 이번주 적립 포인트
    }
} 