package devlava.nudgeapi.controller;

import devlava.nudgeapi.dto.ChartDto;
import devlava.nudgeapi.service.ChartService;
import devlava.nudgeapi.service.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chart")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ChartController {

    private final ResponseService responseService;
    private final ChartService chartService;

    /**
     * 특정 사용자의 전월과 이번달 영업일별 nudgeYn 건수 조회
     */
    @GetMapping("/data")
    public Object getMonthlyNudgeCountByBusinessDays(@RequestParam String userId) {
        try {
            ChartDto chartData = chartService.getMonthlyNudgeCountByBusinessDays(userId);
            return responseService.getSuccessHttpResponseDto(chartData);
        } catch (Exception e) {
            return responseService.getFailHttpResponseDto("차트 데이터 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
