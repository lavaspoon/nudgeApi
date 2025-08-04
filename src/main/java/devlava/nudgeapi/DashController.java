package devlava.nudgeapi;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin("*")
public class DashController {

    private final DashService dashService;

    @GetMapping("/dash")
    public ResponseEntity<DashDto> getDashboardData() {
        try {
            DashDto dashData = dashService.getDashboardData();
            return ResponseEntity.ok(dashData);
        } catch (Exception e) {
            // 로깅 처리
            return ResponseEntity.internalServerError().build();
        }
    }
}