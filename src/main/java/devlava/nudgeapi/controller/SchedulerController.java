package devlava.nudgeapi.controller;

import devlava.nudgeapi.service.NudgePointSchedulerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/scheduler")
@RequiredArgsConstructor
public class SchedulerController {

    private final NudgePointSchedulerService nudgePointSchedulerService;

    /**
     * 수동으로 넛지 포인트 적립 실행
     */
    @PostMapping("/nudge-points/{date}")
    public ResponseEntity<Map<String, Object>> processNudgePointsForDate(@PathVariable String date) {
        try {
            nudgePointSchedulerService.processNudgePointsForDate(date);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", String.format("%s 날짜의 넛지 포인트 적립이 완료되었습니다.", date));
            response.put("processedDate", date);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "넛지 포인트 적립 중 오류가 발생했습니다: " + e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 어제 날짜로 넛지 포인트 적립 실행
     */
    @PostMapping("/nudge-points/yesterday")
    public ResponseEntity<Map<String, Object>> processNudgePointsForYesterday() {
        String yesterday = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return processNudgePointsForDate(yesterday);
    }

    /**
     * 특정 사용자의 특정 날짜 넛지 성공 건수 조회
     */
    @GetMapping("/nudge-count/{skid}/{date}")
    public ResponseEntity<Map<String, Object>> getNudgeSuccessCount(
            @PathVariable String skid,
            @PathVariable String date) {

        try {
            Long count = nudgePointSchedulerService.getNudgeSuccessCount(skid, date);

            Map<String, Object> response = new HashMap<>();
            response.put("skid", skid);
            response.put("date", date);
            response.put("nudgeSuccessCount", count);
            response.put("estimatedPoints", count * 50); // 건당 50포인트

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "넛지 성공 건수 조회 중 오류가 발생했습니다: " + e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 전체 구성원 목록 조회
     */
    @GetMapping("/skids")
    public ResponseEntity<List<String>> getAllSkids() {
        try {
            List<String> skids = nudgePointSchedulerService.getAllSkids();
            return ResponseEntity.ok(skids);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 스케줄러 상태 확인
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getSchedulerStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("schedulerEnabled", true);
        status.put("nextExecution", "매일 새벽 2시");
        status.put("pointsPerNudge", 50);
        status.put("description", "어제 넛지 성공 건수에 따라 건당 50포인트씩 적립");

        return ResponseEntity.ok(status);
    }
}