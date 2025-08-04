package devlava.nudgeapi.entity.data;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "success_stories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuccessStory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "consultant_name", nullable = false)
    private String consultantName; // 상담원 이름

    @Column(name = "consultant_level", nullable = false)
    private String consultantLevel; // 상담원 등급

    @Column(name = "marketing_type", nullable = false)
    private String marketingType; // 마케팅 유형

    @Column(name = "marketing_message", columnDefinition = "TEXT", nullable = false)
    private String marketingMessage; // 마케팅 메시지

    @Column(name = "customer_consent_yn", nullable = false)
    private String customerConsentYn; // 고객 동의 여부

    @Column(name = "success_tip", columnDefinition = "TEXT")
    private String successTip; // 성공 팁

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