package devlava.nudgeapi.controller;

import devlava.nudgeapi.dto.PointInfoDto;
import devlava.nudgeapi.entity.data.PointHistory;
import devlava.nudgeapi.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    /**
     * 통합 포인트 정보 조회 (현재 포인트, 등급, 내역 모두 포함)
     */
    @GetMapping("/{skid}/info")
    public ResponseEntity<PointInfoDto> getPointInfo(@PathVariable String skid) {
        PointInfoDto pointInfo = pointService.getPointInfo(skid);
        return ResponseEntity.ok(pointInfo);
    }

    /**
     * 현재 포인트 잔액 조회
     */
    @GetMapping("/{skid}/balance")
    public ResponseEntity<Map<String, Object>> getCurrentBalance(@PathVariable String skid) {
        int balance = pointService.getCurrentBalance(skid);

        Map<String, Object> response = new HashMap<>();
        response.put("skid", skid);
        response.put("currentBalance", balance);

        return ResponseEntity.ok(response);
    }

    /**
     * 포인트 내역 조회
     */
    @GetMapping("/{skid}/history")
    public ResponseEntity<List<PointHistory>> getPointHistory(@PathVariable String skid) {
        List<PointHistory> history = pointService.getPointHistory(skid);
        return ResponseEntity.ok(history);
    }

    /**
     * 포인트 타입별 내역 조회
     */
    @GetMapping("/{skid}/history/{pointType}")
    public ResponseEntity<List<PointHistory>> getPointHistoryByType(
            @PathVariable String skid,
            @PathVariable String pointType) {
        List<PointHistory> history = pointService.getPointHistoryByType(skid, pointType);
        return ResponseEntity.ok(history);
    }

    /**
     * 포인트 적립
     */
    @PostMapping("/{skid}/earn")
    public ResponseEntity<PointHistory> earnPoints(
            @PathVariable String skid,
            @RequestBody Map<String, Object> request) {

        int amount = (Integer) request.get("amount");
        String reason = (String) request.get("reason");
        String description = (String) request.get("description");

        PointHistory pointHistory = pointService.earnPoints(skid, amount, reason, description);
        return ResponseEntity.ok(pointHistory);
    }

    /**
     * 포인트 사용
     */
    @PostMapping("/{skid}/use")
    public ResponseEntity<PointHistory> usePoints(
            @PathVariable String skid,
            @RequestBody Map<String, Object> request) {

        int amount = (Integer) request.get("amount");
        String reason = (String) request.get("reason");
        String description = (String) request.get("description");

        PointHistory pointHistory = pointService.usePoints(skid, amount, reason, description);
        return ResponseEntity.ok(pointHistory);
    }

    /**
     * 넛지 성공 시 포인트 적립
     */
    @PostMapping("/{skid}/earn/nudge-success")
    public ResponseEntity<PointHistory> earnPointsForNudgeSuccess(@PathVariable String skid) {
        PointHistory pointHistory = pointService.earnPointsForNudgeSuccess(skid);
        return ResponseEntity.ok(pointHistory);
    }

    /**
     * 고객 만족도 우수 시 포인트 적립
     */
    @PostMapping("/{skid}/earn/customer-satisfaction")
    public ResponseEntity<PointHistory> earnPointsForCustomerSatisfaction(@PathVariable String skid) {
        PointHistory pointHistory = pointService.earnPointsForCustomerSatisfaction(skid);
        return ResponseEntity.ok(pointHistory);
    }

    /**
     * 일일 성과 달성 시 포인트 적립
     */
    @PostMapping("/{skid}/earn/daily-achievement")
    public ResponseEntity<PointHistory> earnPointsForDailyAchievement(@PathVariable String skid) {
        PointHistory pointHistory = pointService.earnPointsForDailyAchievement(skid);
        return ResponseEntity.ok(pointHistory);
    }

    /**
     * 주간 성과 1위 시 포인트 적립
     */
    @PostMapping("/{skid}/earn/weekly-first")
    public ResponseEntity<PointHistory> earnPointsForWeeklyFirst(@PathVariable String skid) {
        PointHistory pointHistory = pointService.earnPointsForWeeklyFirst(skid);
        return ResponseEntity.ok(pointHistory);
    }

    /**
     * 월간 우수상담원 시 포인트 적립
     */
    @PostMapping("/{skid}/earn/monthly-excellence")
    public ResponseEntity<PointHistory> earnPointsForMonthlyExcellence(@PathVariable String skid) {
        PointHistory pointHistory = pointService.earnPointsForMonthlyExcellence(skid);
        return ResponseEntity.ok(pointHistory);
    }

    /**
     * 포인트 통계 조회
     */
    @GetMapping("/{skid}/stats")
    public ResponseEntity<Map<String, Object>> getPointStats(@PathVariable String skid) {
        int currentBalance = pointService.getCurrentBalance(skid);
        int totalEarned = pointService.getTotalEarnedPoints(skid);
        int totalUsed = pointService.getTotalUsedPoints(skid);

        Map<String, Object> stats = new HashMap<>();
        stats.put("skid", skid);
        stats.put("currentBalance", currentBalance);
        stats.put("totalEarned", totalEarned);
        stats.put("totalUsed", totalUsed);

        return ResponseEntity.ok(stats);
    }
}