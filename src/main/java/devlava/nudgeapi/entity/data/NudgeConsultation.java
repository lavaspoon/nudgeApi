package devlava.nudgeapi.entity.data;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "nudge_consultations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NudgeConsultation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "consultation_date", nullable = false)
    private String consultationDate; // 상담일

    @Column(name = "skid", nullable = false)
    private String skid; // 사번

    @Column(name = "customer_inquiry", columnDefinition = "TEXT")
    private String customerInquiry; // 고객 문의 사항

    @Column(name = "nudge_yn", nullable = false)
    private String nudgeYn; // 넛지 유무 (Y/N)

    @Column(name = "marketing_type")
    private String marketingType; // 넛지 유형 (GIGA 전환, CRM 전환, TDS 전환)

    @Column(name = "marketing_message", columnDefinition = "TEXT")
    private String marketingMessage; // 넛지 멘트

    @Column(name = "customer_consent_yn")
    private String customerConsentYn; // 고객 넛지 동의 여부 (Y/N)

    @Column(name = "inappropriate_response_yn")
    private String inappropriateResponseYn; // 부정 응대 유무 (Y/N)

    @Column(name = "inappropriate_response_message", columnDefinition = "TEXT")
    private String inappropriateResponseMessage; // 부정 응대 멘트

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 