package devlava.nudgeapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopNudgeUserDto {
    private String userId;
    private String mbName;
    private String deptName;
    private String mbPositionName;
    private String currentGrade;
    private Integer monthNudgeCount;
    private Integer rank;
    private String latestNudgeMessage; // 최근 넛지 멘트
    private String latestNudgeDate; // 최근 넛지 날짜
}
