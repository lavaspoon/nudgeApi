package devlava.nudgeapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointDto {

    private int currentPoints; // 현재 포인트

    /**
     * 이달 나의 넛지 건수에 따른 등급
     * nudgeYn
     * 50건 이하 bronze
     * 100건 이하 silver
     * 150건 이하 gold
     * 151건 이상 platinum
     */
    private String currentGragde; // 현재 나의 등급

    /**
     * 적립 / 지출 내역
     */
    private List<PointHistoryDto> pointHistory; // 포인트 내역

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PointHistoryDto {
        private Integer pointAmount; // 포인트 수량
        private String pointType; // EARN, SPEND
        private String pointReason; // 사유
        private LocalDateTime createdDate; // 생성일시
        private String grade; // 당시 등급
        private BigDecimal gradeBonusRate; // 등급 보너스 비율
    }
}