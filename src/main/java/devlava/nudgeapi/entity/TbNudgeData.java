package devlava.nudgeapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

@Getter
@Entity
@Table(name = "TB_NUDGE_DATA")
public class TbNudgeData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "consulation_date")
    private String consultationDate;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "customer_inquiry")
    private String customerInquiry;

    @Column(name = "nudge_yn")
    private String nudgeYn;

    @Column(name = "marketing_type")
    private String marketingType;

    @Column(name = "marketing_message")
    private String marketingMessage;

    @Column(name = "customer_consent_yn")
    private String customerConsentYn;

    @Column(name = "inappropriate_response_yn")
    private String inappropriateResponseYn;

    @Column(name = "inappropriate_response_message")
    private String inappropriateResponseMessage;
}
