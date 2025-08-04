package devlava.nudgeapi.controller;

import devlava.nudgeapi.dto.DashboardDto;
import devlava.nudgeapi.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * 대시보드 데이터 조회
     */
    @GetMapping("/{skid}")
    public ResponseEntity<DashboardDto> getDashboardData(@PathVariable String skid) {
        DashboardDto dashboardData = dashboardService.getDashboardData(skid);
        return ResponseEntity.ok(dashboardData);
    }
} 