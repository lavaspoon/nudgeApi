package devlava.nudgeapi.entity.data;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "point_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PointHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "skid", nullable = false)
    private String skid; // 사번

    @Column(name = "point_type", nullable = false)
    private String pointType; // EARN(적립), USE(사용)

    @Column(name = "point_amount", nullable = false)
    private Integer pointAmount; // 포인트 금액

    @Column(name = "point_reason", nullable = false)
    private String pointReason; // 포인트 적립/사용 사유

    @Column(name = "point_description", columnDefinition = "TEXT")
    private String pointDescription; // 상세 설명

    @Column(name = "balance_after", nullable = false)
    private Integer balanceAfter; // 적립/사용 후 잔액

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}