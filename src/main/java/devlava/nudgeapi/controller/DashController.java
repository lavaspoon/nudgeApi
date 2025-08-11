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
public class DashController {

    private final ResponseService responseService;

    private final PointService pointService;
    private final PointSchedulerService pointSchedulerService;
    private final TbNudgeDataRepository nudgeDataRepository;
    private final TbLmsMemberRepository memberRepository;

    @GetMapping("/point/{userId}")
    public ResponseEntity<HttpResponseDto> getPoint(@PathVariable String userId) {
        PointDto result = pointService.getPoint(userId);
        HttpResponseDto responseDto = responseService.getSuccessHttpResponseDto(result);

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/test-scheduler")
    public ResponseEntity<HttpResponseDto> testScheduler() {
        pointSchedulerService.dailyPointReward();
        HttpResponseDto responseDto = responseService.getSuccessHttpResponseDto("스케줄러 테스트 완료");
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/test-scheduler-date/{dateStr}")
    public ResponseEntity<HttpResponseDto> testSchedulerWithDate(@PathVariable String dateStr) {
        // 특정 날짜로 포인트 지급 테스트
        System.out.println("=== 특정 날짜 스케줄러 테스트 ===");
        System.out.println("테스트 날짜: " + dateStr);

        // 8월 11일에 실제 넛지 활동을 한 사용자들
        String[] testUsers = { "royal1_mgr01", "csm3_mgr01", "csm5_mgr03" };

        for (String userId : testUsers) {
            try {
                System.out.println("\n--- " + userId + " 사용자 포인트 지급 테스트 ---");
                pointService.calculateAndRewardDailyPoints(userId, dateStr);
                System.out.println("포인트 지급 완료: " + userId);
            } catch (Exception e) {
                System.out.println("포인트 지급 실패: " + userId + " - " + e.getMessage());
                e.printStackTrace();
            }
        }

        HttpResponseDto responseDto = responseService.getSuccessHttpResponseDto("특정 날짜 스케줄러 테스트 완료");
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/run-scheduler")
    public ResponseEntity<HttpResponseDto> runScheduler(@RequestParam String dateStr) {
        // 특정 날짜로 전체 스케줄러 실행 (실제 스케줄러와 동일한 로직)
        System.out.println("=== 전체 스케줄러 실행 ===");
        System.out.println("실행 날짜: " + dateStr);

        try {
            // 실제 스케줄러와 동일한 로직으로 실행
            pointSchedulerService.dailyPointReward();

            HttpResponseDto responseDto = responseService.getSuccessHttpResponseDto(
                    "스케줄러 실행 완료 - 날짜: " + dateStr);
            return ResponseEntity.ok(responseDto);

        } catch (Exception e) {
            System.out.println("스케줄러 실행 실패: " + e.getMessage());
            e.printStackTrace();

            HttpResponseDto responseDto = responseService.getFailHttpResponseDto(
                    "스케줄러 실행 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body(responseDto);
        }
    }

    @PostMapping("/run-scheduler-custom-date")
    public ResponseEntity<HttpResponseDto> runSchedulerWithCustomDate(@RequestParam String dateStr) {
        // 특정 날짜로 커스텀 스케줄러 실행
        System.out.println("=== 커스텀 날짜 스케줄러 실행 ===");
        System.out.println("실행 날짜: " + dateStr);

        try {
            // 어제 날짜 대신 파라미터로 받은 날짜 사용
            // 포인트 지급 대상자 조회: 실장과 상담매니저만
            List<TbLmsMember> targetMembers = memberRepository.findByComCodeIn(Arrays.asList("35", "20"));

            int successCount = 0;
            int errorCount = 0;

            // 각 대상자별로 포인트 지급 처리
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

    @GetMapping("/test-data")
    public ResponseEntity<HttpResponseDto> testData() {
        // 실제 데이터베이스의 날짜 형식을 확인하기 위한 테스트
        String testDate = "20250811";
        String testMonth = "202508";

        // 8월 11일에 실제 넛지 활동을 한 사용자들 테스트
        String[] testUsers = { "royal1_mgr01", "csm3_mgr01", "csm5_mgr03" };

        System.out.println("=== 데이터 테스트 ===");
        System.out.println("테스트 날짜: " + testDate);
        System.out.println("테스트 월: " + testMonth);

        for (String testUserId : testUsers) {
            System.out.println("\n--- " + testUserId + " 사용자 테스트 ---");

            // 실제 쿼리 실행
            Integer dailyCount = nudgeDataRepository.countNudgeByUserIdAndDate(testUserId, testDate);
            Integer monthlyCount = nudgeDataRepository.countMonthlyNudgeByUserId(testUserId, testMonth);

            System.out.println("일일 넛지 건수: " + dailyCount);
            System.out.println("월간 넛지 건수: " + monthlyCount);

            // 포인트 내역도 확인
            PointDto pointInfo = pointService.getPoint(testUserId);
            System.out.println("현재 포인트: " + pointInfo.getCurrentPoints());
            System.out.println("현재 등급: " + pointInfo.getCurrentGragde());
            System.out.println(
                    "포인트 내역 개수: " + (pointInfo.getPointHistory() != null ? pointInfo.getPointHistory().size() : 0));
        }

        HttpResponseDto responseDto = responseService.getSuccessHttpResponseDto("데이터 테스트 완료 - 콘솔 로그 확인");
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/test-point-history/{userId}")
    public ResponseEntity<HttpResponseDto> testPointHistory(@PathVariable String userId) {
        System.out.println("=== " + userId + " 사용자 포인트 내역 테스트 ===");

        PointDto pointInfo = pointService.getPoint(userId);

        System.out.println("현재 포인트: " + pointInfo.getCurrentPoints());
        System.out.println("현재 등급: " + pointInfo.getCurrentGragde());
        System.out.println(
                "포인트 내역 개수: " + (pointInfo.getPointHistory() != null ? pointInfo.getPointHistory().size() : 0));

        if (pointInfo.getPointHistory() != null && !pointInfo.getPointHistory().isEmpty()) {
            System.out.println("\n--- 포인트 내역 상세 ---");
            for (int i = 0; i < pointInfo.getPointHistory().size(); i++) {
                PointDto.PointHistoryDto history = pointInfo.getPointHistory().get(i);
                System.out.println((i + 1) + ". " + history.getPointAmount() + "포인트 (" + history.getPointType() + ") - "
                        + history.getPointReason() + " [" + history.getCreatedDate() + "]");
            }
        } else {
            System.out.println("포인트 내역이 없습니다.");
        }

        HttpResponseDto responseDto = responseService.getSuccessHttpResponseDto(pointInfo);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/debug-data/{userId}")
    public ResponseEntity<HttpResponseDto> debugData(@PathVariable String userId) {
        System.out.println("=== " + userId + " 사용자 데이터 디버깅 ===");

        // 8월과 9월 데이터 조회
        String augustMonth = "202508";
        String septemberMonth = "202509";

        Integer augustCount = nudgeDataRepository.countMonthlyNudgeByUserId(userId, augustMonth);
        Integer septemberCount = nudgeDataRepository.countMonthlyNudgeByUserId(userId, septemberMonth);

        System.out.println("8월 총 넛지 건수: " + augustCount);
        System.out.println("9월 총 넛지 건수: " + septemberCount);

        // 9월 1일 데이터 조회
        String septemberFirst = "20250901";
        Integer septemberFirstCount = nudgeDataRepository.countNudgeByUserIdAndDate(userId, septemberFirst);
        System.out.println("9월 1일 넛지 건수: " + septemberFirstCount);

        // 포인트 내역 확인
        PointDto pointInfo = pointService.getPoint(userId);
        System.out.println("현재 등급: " + pointInfo.getCurrentGragde());
        System.out.println("총 포인트: " + pointInfo.getCurrentPoints());
        System.out.println("월간 넛지 건수: " + pointInfo.getMonthNudgeCount());

        HttpResponseDto responseDto = responseService.getSuccessHttpResponseDto(
                "디버깅 완료 - 8월: " + augustCount + ", 9월: " + septemberCount + ", 9월1일: " + septemberFirstCount);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/test-monthly-reset")
    public ResponseEntity<HttpResponseDto> testMonthlyReset(@RequestParam String userId, @RequestParam String dateStr) {
        System.out.println("=== 월별 초기화 테스트 ===");
        System.out.println("사용자: " + userId);
        System.out.println("테스트 날짜: " + dateStr);

        // 현재 사용자 정보 조회
        PointDto beforePointInfo = pointService.getPoint(userId);
        System.out.println("초기화 전 - 등급: " + beforePointInfo.getCurrentGragde() + ", 총 포인트: "
                + beforePointInfo.getCurrentPoints() + ", 월간 넛지 건수: " + beforePointInfo.getMonthNudgeCount());

        // 포인트 지급 실행 (월별 초기화 로직 포함)
        pointService.calculateAndRewardDailyPoints(userId, dateStr);

        // 초기화 후 사용자 정보 조회
        PointDto afterPointInfo = pointService.getPoint(userId);
        System.out.println("초기화 후 - 등급: " + afterPointInfo.getCurrentGragde() + ", 총 포인트: "
                + afterPointInfo.getCurrentPoints() + ", 월간 넛지 건수: " + afterPointInfo.getMonthNudgeCount());

        HttpResponseDto responseDto = responseService.getSuccessHttpResponseDto(
                "월별 초기화 테스트 완료 - 사용자: " + userId + ", 날짜: " + dateStr);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/test-date-format")
    public ResponseEntity<HttpResponseDto> testDateFormat(@RequestParam String dateStr) {
        System.out.println("=== 날짜 형식 테스트 ===");
        System.out.println("입력 날짜: " + dateStr);

        // 날짜 변환 테스트
        String readableDate = convertToReadableDate(dateStr);
        System.out.println("변환된 날짜: " + readableDate);

        // 예상 포인트 내역 메시지
        String expectedMessage = readableDate + " 넛지 활동 보상 (2건)";
        System.out.println("예상 포인트 내역: " + expectedMessage);

        HttpResponseDto responseDto = responseService.getSuccessHttpResponseDto(
                "날짜 변환 테스트 완료 - 입력: " + dateStr + ", 변환: " + readableDate + ", 예상 메시지: " + expectedMessage);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 날짜 문자열을 읽기 쉬운 형태로 변환
     * 
     * @param dateStr YYYYMMDD 형식의 날짜 문자열 (예: "20250817")
     * @return 읽기 쉬운 형태의 날짜 문자열 (예: "8월17일")
     */
    private String convertToReadableDate(String dateStr) {
        if (dateStr == null || dateStr.length() != 8) {
            return "알 수 없는 날짜";
        }

        try {
            int year = Integer.parseInt(dateStr.substring(0, 4));
            int month = Integer.parseInt(dateStr.substring(4, 6));
            int day = Integer.parseInt(dateStr.substring(6, 8));

            return month + "월" + day + "일";
        } catch (NumberFormatException e) {
            return "알 수 없는 날짜";
        }
    }
}
