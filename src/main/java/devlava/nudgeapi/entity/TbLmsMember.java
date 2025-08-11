package devlava.nudgeapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "TB_LMS_MEMBER")
public class TbLmsMember {

    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "company")
    private String company;

    @Column(name = "mb_name")
    private String mbName;

    @Column(name = "mb_position")
    private Integer mbPosition;

    @Column(name = "dept_name")
    private String deptName;

    @Column(name = "mb_position_name")
    private String mbPositionName;

    @Column(name = "mb_status")
    private Integer mbStatus;

    @Column(name = "email")
    private String email;

    @Column(name = "use_yn")
    private String useYn;

    @Column(name = "dept_idx")
    private Integer deptIdx;

    @Column(name = "revel")
    private String revel;

    @Column(name = "com_code")
    private String comCode;

    public TbLmsMember(String comCode, String company, Integer deptIdx, String deptName, String email, String mbName, Integer mbPosition, String mbPositionName, Integer mbStatus, String revel, String userId, String useYn) {
        this.comCode = comCode;
        this.company = company;
        this.deptIdx = deptIdx;
        this.deptName = deptName;
        this.email = email;
        this.mbName = mbName;
        this.mbPosition = mbPosition;
        this.mbPositionName = mbPositionName;
        this.mbStatus = mbStatus;
        this.revel = revel;
        this.userId = userId;
        this.useYn = useYn;
    }
}
