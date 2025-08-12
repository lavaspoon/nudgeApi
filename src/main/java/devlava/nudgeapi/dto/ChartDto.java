package devlava.nudgeapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChartDto {

    // 전월 영업일별 nudgeYn 건수
    private Map<String, Integer> lastMonthNudgeCount;

    // 이번달 영업일별 nudgeYn 건수
    private Map<String, Integer> currentMonthNudgeCount;

    // 추가: 전월 총 건수
    private Integer lastMonthTotal;

    // 추가: 이번달 총 건수
    private Integer currentMonthTotal;
}
