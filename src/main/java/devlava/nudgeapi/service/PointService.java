package devlava.nudgeapi.service;

import devlava.nudgeapi.dto.PointDto;
import devlava.nudgeapi.dto.PointGrade;
import devlava.nudgeapi.entity.TbNudgePoint;
import devlava.nudgeapi.entity.TbUserPointSummary;
import devlava.nudgeapi.repository.TbNudgeDataRepository;
import devlava.nudgeapi.repository.TbNudgePointRepository;
import devlava.nudgeapi.repository.TbUserPointSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class PointService {

    private final TbNudgePointRepository nudgePointRepository;
    private final TbUserPointSummaryRepository userPointSummaryRepository;
    private final TbNudgeDataRepository nudgeDataRepository;

    /**
     * 넛지 1건당 기본 지급 포인트
     * 모든 등급에 공통 적용되는 기본 포인트
     */
    private static final int BASE_POINT_PER_NUDGE = 50;

    /**
     * 사용자의 현재 포인트 정보 조회
     * @param userId 사용자 ID
     * @return 포인트 정보 (현재 보유 포인트, 등급)
     */
    public PointDto getPoint(String userId) {
        TbUserPointSummary summary = userPointSummaryRepository.findByUserId(userId)
                .orElse(createDefaultSummary(userId));

        return PointDto.builder()
                .currentPoints(summary.getTotalPoints() != null ? summary.getTotalPoints() : 0)
                .currentGragde(summary.getCurrentGrade() != null ? summary.getCurrentGrade() : "bronze")
                .build();
    }

    /**
     * 특정 날짜의 넛지 활동에 대한 포인트 계산 및 지급
     *
     * @param userId 사용자 ID
     * @param dateStr 대상 날짜 (yyyyMMdd 형식, 예: "20250811")
     *
     * 처리 과정:
     * 1. 해당 날짜의 넛지 건수 조회
     * 2. 이달 누적 넛지 건수로 등급 판정
     * 3. 기본 포인트 + 등급별 보너스 포인트 계산
     * 4. 포인트 지급 내역 저장
     * 5. 사용자 포인트 요약 정보 업데이트
     */
    public void calculateAndRewardDailyPoints(String userId, String dateStr) {
        // Step 1: 해당 날짜의 넛지 건수 조회
        // TB_NUDGE_DATA에서 nudge_yn='Y'이고 consulation_date가 dateStr로 시작하는 레코드 수 조회
        // 예: dateStr="20250811"이면 "20250811%" 패턴으로 검색하여 8월 11일의 모든 넛지 건수 조회
        Integer dailyNudgeCount = nudgeDataRepository.countNudgeByUserIdAndDate(userId, dateStr);

        // 넛지 건수가 없으면 포인트 지급 없이 종료
        if (dailyNudgeCount == null || dailyNudgeCount == 0) {
            return;
        }

        // Step 2: 이달 총 넛지 건수 조회 (등급 계산용)
        // dateStr에서 앞 6자리만 추출하여 년월 정보 생성 (예: "20250811" → "202508")
        String monthPrefix = dateStr.substring(0, 6); // YYYYMM
        Integer monthlyNudgeCount = nudgeDataRepository.countMonthlyNudgeByUserId(userId, monthPrefix);

        // Step 3: 이달 누적 넛지 건수를 기준으로 현재 등급 계산
        // PointGrade.getGradeByNudgeCount() 메서드로 등급 결정
        // - 0~50건: Bronze (보너스 0%)
        // - 51~100건: Silver (보너스 10%)
        // - 101~150건: Gold (보너스 20%)
        // - 151건 이상: Platinum (보너스 30%)
        PointGrade currentGrade = PointGrade.getGradeByNudgeCount(monthlyNudgeCount != null ? monthlyNudgeCount : 0);

        // Step 4: 포인트 계산
        // 기본 포인트 = 일일 넛지 건수 × 50포인트
        int basePoints = dailyNudgeCount * BASE_POINT_PER_NUDGE;

        // 등급별 보너스 포인트 = 기본 포인트 × 등급별 보너스 비율
        // 예: Silver 등급(10% 보너스)에서 기본 포인트가 200이면 → 200 × 0.1 = 20 보너스 포인트
        int bonusPoints = (int) (basePoints * currentGrade.getBonusRate());

        // 최종 지급 포인트 = 기본 포인트 + 보너스 포인트
        int totalPoints = basePoints + bonusPoints;

        // Step 5: 포인트 적립 기록을 TB_NUDGE_POINT 테이블에 저장
        // 나중에 포인트 내역 조회 시 사용되는 상세 기록
        TbNudgePoint pointRecord = TbNudgePoint.builder()
                .userId(userId)                                                    // 사용자 ID
                .pointAmount(totalPoints)                                          // 지급 포인트 수량
                .pointType("EARN")                                                 // 적립 구분 (EARN/SPEND)
                .pointReason("일일 넛지 활동 보상 (" + dailyNudgeCount + "건)")        // 적립 사유
                .nudgeCount(dailyNudgeCount)                                       // 해당일 넛지 건수
                .grade(currentGrade.getGradeName())                                // 적립 시점의 등급
                .gradeBonusRate(BigDecimal.valueOf(currentGrade.getBonusRate()))   // 등급별 보너스 비율
                .build();

        nudgePointRepository.save(pointRecord);

        // Step 6: 사용자 포인트 요약 정보 업데이트
        // TB_USER_POINT_SUMMARY 테이블에서 해당 사용자 정보 조회 (없으면 기본값 생성)
        TbUserPointSummary summary = userPointSummaryRepository.findByUserId(userId)
                .orElse(createDefaultSummary(userId));

        // 총 보유 포인트에 금일 지급 포인트 추가
        summary.addPoints(totalPoints);

        // 현재 등급을 이달 누적 넛지 건수 기준으로 업데이트
        summary.updateGrade(currentGrade.getGradeName());

        // 이달 누적 넛지 건수 업데이트
        summary.updateMonthNudgeCount(monthlyNudgeCount);

        // 업데이트된 요약 정보 저장
        userPointSummaryRepository.save(summary);
    }

    /**
     * 신규 사용자를 위한 기본 포인트 요약 정보 생성
     * @param userId 사용자 ID
     * @return 기본값으로 초기화된 포인트 요약 정보
     */
    private TbUserPointSummary createDefaultSummary(String userId) {
        return TbUserPointSummary.builder()
                .userId(userId)
                .totalPoints(0)           // 초기 포인트 0
                .currentGrade("bronze")   // 초기 등급 Bronze
                .monthNudgeCount(0)       // 초기 넛지 건수 0
                .build();
    }
}