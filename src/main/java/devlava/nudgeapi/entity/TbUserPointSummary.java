package devlava.nudgeapi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "TB_USER_POINT_SUMMARY")
public class TbUserPointSummary {

    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "total_points")
    private Integer totalPoints; // 총 보유 포인트

    @Column(name = "current_grade")
    private String currentGrade; // 현재 등급

    @Column(name = "month_nudge_count")
    private Integer monthNudgeCount; // 이달 넛지 건수

    @Column(name = "last_processed_month")
    private String lastProcessedMonth; // 마지막으로 처리한 월 (YYYYMM 형식)

    @Column(name = "updated_date")
    private LocalDateTime updatedDate; // 최종 업데이트 일시

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
    }

    public void addPoints(Integer points) {
        this.totalPoints = (this.totalPoints != null ? this.totalPoints : 0) + points;
    }

    public void updateGrade(String grade) {
        this.currentGrade = grade;
    }

    public void updateMonthNudgeCount(Integer count) {
        this.monthNudgeCount = count;
    }

    public void updateLastProcessedMonth(String month) {
        this.lastProcessedMonth = month;
    }

    /**
     * 월별 데이터 초기화 (새로운 월이 시작될 때)
     */
    public void resetMonthlyData() {
        this.currentGrade = "bronze"; // 등급을 Bronze로 초기화
        this.monthNudgeCount = 0; // 월간 넛지 건수를 0으로 초기화
    }
}