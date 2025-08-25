package devlava.nudgeapi.controller;

import devlava.nudgeapi.dto.HttpErrorResponse;
import devlava.nudgeapi.dto.HttpResponseDto;
import devlava.nudgeapi.dto.PointDto;
import devlava.nudgeapi.dto.DashboardDto;
import devlava.nudgeapi.entity.TbLmsMember;
import devlava.nudgeapi.entity.TbLmsDept;
import devlava.nudgeapi.repository.TbLmsMemberRepository;
import devlava.nudgeapi.repository.TbLmsDeptRepository;
import devlava.nudgeapi.repository.TbNudgeDataRepository;
import devlava.nudgeapi.service.PointService;
import devlava.nudgeapi.service.PointSchedulerService;
import devlava.nudgeapi.service.ResponseService;
import devlava.nudgeapi.config.DeptConfig;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
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
    private final TbLmsDeptRepository deptRepository;
    private final TbNudgeDataRepository nudgeDataRepository;
    private final DeptConfig deptConfig;

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
            List<TbLmsMember> targetMembers = memberRepository.findByComCodeIn(deptConfig.getPointTargetCompCodes());

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

    /**
     * 나의 이달 넛지 건수 순위 조회
     * 
     * @param userId 사용자 ID
     * @return 순위 정보 (내 순위, 전체 구성원 수, 상위 5명 등)
     */
    @GetMapping("/rank/{userId}")
    public ResponseEntity<HttpResponseDto> getMyRank(@PathVariable String userId) {
        try {
            DashboardDto.RankInfoDto rankInfo = calculateMyRank(userId);
            HttpResponseDto responseDto = responseService.getSuccessHttpResponseDto(rankInfo);
            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            System.out.println("순위 조회 실패: " + e.getMessage());
            e.printStackTrace();
            HttpResponseDto responseDto = responseService.getFailHttpResponseDto("순위 조회 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body(responseDto);
        }
    }

    /**
     * 사용자의 이달 넛지 건수 순위 계산
     * 
     * @param userId 사용자 ID
     * @return 순위 정보
     */
    private DashboardDto.RankInfoDto calculateMyRank(String userId) {
        // 현재 월 계산
        String currentMonth = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMM"));

        // 순위 계산 대상 부서들 조회
        List<Integer> targetDeptIds = deptConfig.getRankingTargetDepts();

        // 해당 부서들의 이번달 넛지 건수 순위 조회
        List<Object[]> rankingData = nudgeDataRepository.findNudgeRankingByDeptIds(targetDeptIds, currentMonth);

        // 내 넛지 건수 조회
        Integer myNudgeCount = nudgeDataRepository.countUserMonthlyNudge(userId, currentMonth);
        if (myNudgeCount == null)
            myNudgeCount = 0;

        // 순위 계산
        int myRank = 1;
        int totalMembers = rankingData.size();
        int topNudgeCount = 0;

        for (int i = 0; i < rankingData.size(); i++) {
            Object[] data = rankingData.get(i);
            String rankUserId = (String) data[0];
            int nudgeCount = ((Number) data[1]).intValue();

            if (i == 0) {
                topNudgeCount = nudgeCount; // 1위 넛지 건수
            }

            if (rankUserId.equals(userId)) {
                myRank = i + 1; // 내 순위 (1부터 시작)
                break;
            }
        }

        // 순위 백분율 계산 (상위 %)
        double rankPercentage = totalMembers > 0 ? ((double) myRank / totalMembers) * 100 : 0;

        // 상위 5명 정보 구성
        List<DashboardDto.TopRankerDto> topRankers = new ArrayList<>();
        for (int i = 0; i < Math.min(5, rankingData.size()); i++) {
            Object[] data = rankingData.get(i);
            String rankUserId = (String) data[0];
            int nudgeCount = ((Number) data[1]).intValue();

            // 사용자 정보 조회
            TbLmsMember member = memberRepository.findById(rankUserId).orElse(null);
            if (member != null) {
                topRankers.add(DashboardDto.TopRankerDto.builder()
                        .userId(rankUserId)
                        .userName(member.getMbName())
                        .deptName(member.getDeptName())
                        .nudgeCount(nudgeCount)
                        .rank(i + 1)
                        .build());
            }
        }

        return DashboardDto.RankInfoDto.builder()
                .myRank(myRank)
                .totalMembers(totalMembers)
                .myNudgeCount(myNudgeCount)
                .topNudgeCount(topNudgeCount)
                .rankPercentage(Math.round(rankPercentage * 100.0) / 100.0) // 소수점 둘째자리까지
                .topRankers(topRankers)
                .build();
    }
}
