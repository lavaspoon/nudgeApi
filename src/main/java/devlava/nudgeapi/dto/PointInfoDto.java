package devlava.nudgeapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointInfoDto {
    
    // 현재 포인트 정보
    private int currentPoints; // 현재 포인트
    private String currentGrade; // 현재 등급
    private String nextGrade; // 다음 등급
    private int gradeProgress; // 등급 진행률
    private int teamRank; // 팀 내 순위
    private int weeklyEarned; // 이번주 적립 포인트
    
    // 포인트 통계
    private int totalEarned; // 총 적립 포인트
    private int totalUsed; // 총 사용 포인트
    
    // 포인트 내역
    private List<PointHistoryDto> earnHistory; // 적립 내역
    private List<PointHistoryDto> useHistory; // 사용 내역
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PointHistoryDto {
        private Long id;
        private String pointType; // EARN 또는 USE
        private int pointAmount; // 포인트 금액
        private String pointReason; // 포인트 적립/사용 사유
        private String pointDescription; // 상세 설명
        private int balanceAfter; // 적립/사용 후 잔액
        private String createdAt; // 생성일시
    }
} 