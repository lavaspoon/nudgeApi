package devlava.nudgeapi.controller;

import devlava.nudgeapi.dto.HttpErrorResponse;
import devlava.nudgeapi.dto.HttpResponseDto;
import devlava.nudgeapi.dto.PointDto;
import devlava.nudgeapi.entity.TbLmsMember;
import devlava.nudgeapi.repository.TbLmsMemberRepository;
import devlava.nudgeapi.repository.TbNudgeDataRepository;
import devlava.nudgeapi.service.PointService;
import devlava.nudgeapi.service.PointSchedulerService;
import devlava.nudgeapi.service.ResponseService;

import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dash")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class DashController {

    private final ResponseService responseService;

    private final PointService pointService;
    private final TbLmsMemberRepository memberRepository;

    @GetMapping("/point/{userId}")
    public ResponseEntity<HttpResponseDto> getPoint(@PathVariable String userId) {
        PointDto result = pointService.getPoint(userId);
        HttpResponseDto responseDto = responseService.getSuccessHttpResponseDto(result);

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/run-scheduler-date")
    public ResponseEntity<HttpResponseDto> runSchedulerWithCustomDate(@RequestParam String dateStr) {
        // 특정 날짜로 커스텀 스케줄러 실행
        System.out.println("=== 커스텀 날짜 스케줄러 실행 ===");
        System.out.println("실행 날짜: " + dateStr);

        try {
            List<TbLmsMember> targetMembers = memberRepository.findByComCodeIn(Arrays.asList("35", "20"));

            int successCount = 0;
            int errorCount = 0;

            for (TbLmsMember member : targetMembers) {
                try {
                    System.out.println(
                            "포인트 지급 시작: " + member.getUserId() + " (" + member.getMbName() + ") - 대상 날짜: " + dateStr);
                    pointService.calculateAndRewardDailyPoints(member.getUserId(), dateStr);
                    successCount++;
                    System.out.println("포인트 지급 완료: " + member.getUserId() + " (" + member.getMbName() + ")");
                } catch (Exception e) {
                    errorCount++;
                    System.out.println("포인트 지급 실패: " + member.getUserId() + " (" + member.getMbName() + "), 오류: "
                            + e.getMessage());
                }
            }

            System.out.println("커스텀 스케줄러 완료 - 성공: " + successCount + "건, 실패: " + errorCount + "건");

            HttpResponseDto responseDto = responseService.getSuccessHttpResponseDto(
                    "커스텀 스케줄러 실행 완료 - 날짜: " + dateStr + ", 성공: " + successCount + "건, 실패: " + errorCount + "건");
            return ResponseEntity.ok(responseDto);

        } catch (Exception e) {
            System.out.println("커스텀 스케줄러 실행 실패: " + e.getMessage());
            e.printStackTrace();

            HttpResponseDto responseDto = responseService.getFailHttpResponseDto(
                    "커스텀 스케줄러 실행 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body(responseDto);
        }
    }
}
