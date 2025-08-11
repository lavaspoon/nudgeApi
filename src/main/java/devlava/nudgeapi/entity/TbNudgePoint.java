package devlava.nudgeapi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "TB_NUDGE_POINT")
public class TbNudgePoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private String userId; // 사용자 ID

    @Column(name = "point_amount")
    private Integer pointAmount; // 포인트 수량 (양수: 적립, 음수: 지출)

    @Column(name = "point_type")
    private String pointType; // 포인트 타입 (EARN: 적립, SPEND: 지출)

    @Column(name = "point_reason")
    private String pointReason; // 적립/지출 사유

    @Column(name = "created_date")
    private LocalDateTime createdDate; // 생성일시

    @Column(name = "nudge_count")
    private Integer nudgeCount; // 해당일 넛지 건수

    @Column(name = "grade")
    private String grade; // 적립 시 등급

    @Column(name = "grade_bonus_rate")
    private BigDecimal gradeBonusRate; // 등급별 보너스 비율

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
    }
}