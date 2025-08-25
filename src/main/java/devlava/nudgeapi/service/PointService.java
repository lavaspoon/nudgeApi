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
import java.util.List;

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
     * 넛지 성공 시 추가 지급 포인트
     * customerConsentYn이 'Y'인 경우 추가 지급
     */
    private static final int SUCCESS_BONUS_POINT = 30;

    /**
     * 사용자의 현재 포인트 정보 조회
     * 
     * @param userId 사용자 ID
     * @return 포인트 정보 (현재 보유 포인트, 등급, 포인트 내역)
     */
    public PointDto getPoint(String userId) {
        TbUserPointSummary summary = userPointSummaryRepository.findByUserId(userId)
                .orElse(createDefaultSummary(userId));

        // 사용자의 포인트 내역 조회 (최신순)
        List<TbNudgePoint> pointHistory = nudgePointRepository.findByUserIdOrderByCreatedDateDesc(userId);

        // PointHistoryDto 리스트로 변환
        List<PointDto.PointHistoryDto> historyDtoList = pointHistory.stream()
                .map(point -> PointDto.PointHistoryDto.builder()
                        .pointAmount(point.getPointAmount())
                        .pointType(point.getPointType())
                        .pointReason(point.getPointReason())
                        .createdDate(point.getCreatedDate())
                        .grade(point.getGrade())
                        .gradeBonusRate(point.getGradeBonusRate())
                        .build())
                .toList();

        return PointDto.builder()
                .currentPoints(summary.getTotalPoints() != null ? summary.getTotalPoints() : 0)
                .currentGragde(summary.getCurrentGrade() != null ? summary.getCurrentGrade() : "bronze")
                .monthNudgeCount(summary.getMonthNudgeCount() != null ? summary.getMonthNudgeCount() : 0)
                .pointHistory(historyDtoList)
                .build();
    }

    /**
     * 특정 날짜의 넛지 활동에 대한 포인트 계산 및 지급
     *
     * @param userId  사용자 ID
     * @param dateStr 대상 날짜 (yyyyMMdd 형식, 예: "20250811")
     *
     *                처리 과정:
     *                1. 해당 날짜의 넛지 건수 조회
     *                2. 이달 누적 넛지 건수로 등급 판정
     *                3. 기본 포인트 + 등급별 보너스 포인트 계산
     *                4. 포인트 지급 내역 저장
     *                5. 사용자 포인트 요약 정보 업데이트
     */
    public void calculateAndRewardDailyPoints(String userId, String dateStr) {
        // Step 1: 해당 날짜의 넛지 건수 및 성공 건수 조회
        // TB_NUDGE_DATA에서 nudge_yn='Y'이고 consulation_date가 dateStr로 시작하는 레코드 수 조회
        // 예: dateStr="20250811"이면 "20250811%" 패턴으로 검색하여 8월 11일의 모든 넛지 건수 조회
        System.out.println("DEBUG: " + userId + " 사용자의 " + dateStr + " 날짜로 넛지 건수 조회 시작");
        Integer dailyNudgeCount = nudgeDataRepository.countNudgeByUserIdAndDate(userId, dateStr);
        System.out.println("DEBUG: " + userId + " 사용자의 일일 넛지 건수: " + dailyNudgeCount);

        // 넛지 건수가 없으면 포인트 지급 없이 종료
        if (dailyNudgeCount == null || dailyNudgeCount == 0) {
            System.out.println("DEBUG: " + userId + " 사용자의 " + dateStr + " 날짜에 넛지 건수가 없습니다.");
            return;
        }

        // 성공 건수 조회 (customerConsentYn = 'Y')
        Integer dailySuccessCount = nudgeDataRepository.countNudgeSuccessByUserIdAndDate(userId, dateStr);
        System.out.println("DEBUG: " + userId + " 사용자의 일일 넛지 성공 건수: " + dailySuccessCount);

        // Step 2: 이달 총 넛지 건수 조회 (등급 계산용)
        // dateStr에서 앞 6자리만 추출하여 년월 정보 생성 (예: "20250811" → "202508")
        String monthPrefix = dateStr.substring(0, 6); // YYYYMM
        System.out.println("DEBUG: " + userId + " 사용자의 " + monthPrefix + " 월간 넛지 건수 조회 시작");
        Integer monthlyNudgeCount = nudgeDataRepository.countMonthlyNudgeByUserId(userId, monthPrefix);
        System.out.println("DEBUG: " + userId + " 사용자의 월간 넛지 건수: " + monthlyNudgeCount);

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

        // 성공 보너스 포인트 = 일일 성공 건수 × 30포인트
        int successBonusPoints = (dailySuccessCount != null ? dailySuccessCount : 0) * SUCCESS_BONUS_POINT;

        // 등급별 보너스 포인트 = 기본 포인트 × 등급별 보너스 비율
        // 예: Silver 등급(10% 보너스)에서 기본 포인트가 200이면 → 200 × 0.1 = 20 보너스 포인트
        int gradeBonusPoints = (int) (basePoints * currentGrade.getBonusRate());

        // 최종 지급 포인트 = 기본 포인트 + 성공 보너스 포인트 + 등급별 보너스 포인트
        int totalPoints = basePoints + successBonusPoints + gradeBonusPoints;
        System.out.println("DEBUG: " + userId + " 사용자의 포인트 계산 완료 - 기본: " + basePoints + ", 성공보너스: " + successBonusPoints
                + ", 등급보너스: " + gradeBonusPoints
                + ", 총합: " + totalPoints);

        // Step 5: 포인트 적립 기록을 TB_NUDGE_POINT 테이블에 저장
        // 나중에 포인트 내역 조회 시 사용되는 상세 기록
        System.out.println("DEBUG: " + userId + " 사용자의 포인트 기록 저장 시작");

        // 날짜 문자열을 읽기 쉬운 형태로 변환 (예: "20250817" → "8월17일")
        String readableDate = convertToReadableDate(dateStr);

        // 포인트 적립 사유 구성
        String pointReason = readableDate + " 넛지 활동 보상 (" + dailyNudgeCount + "건)";
        if (dailySuccessCount != null && dailySuccessCount > 0) {
            pointReason += " + 성공 보너스 (" + dailySuccessCount + "건)";
        }

        TbNudgePoint pointRecord = TbNudgePoint.builder()
                .userId(userId) // 사용자 ID
                .pointAmount(totalPoints) // 지급 포인트 수량
                .pointType("EARN") // 적립 구분 (EARN/SPEND)
                .pointReason(pointReason) // 적립 사유
                .nudgeCount(dailyNudgeCount) // 해당일 넛지 건수
                .grade(currentGrade.getGradeName()) // 적립 시점의 등급
                .gradeBonusRate(BigDecimal.valueOf(currentGrade.getBonusRate())) // 등급별 보너스 비율
                .build();

        nudgePointRepository.save(pointRecord);
        System.out.println("DEBUG: " + userId + " 사용자의 포인트 기록 저장 완료");

        // Step 6: 사용자 포인트 요약 정보 업데이트
        // TB_USER_POINT_SUMMARY 테이블에서 해당 사용자 정보 조회 (없으면 기본값 생성)
        System.out.println("DEBUG: " + userId + " 사용자의 포인트 요약 정보 업데이트 시작");
        TbUserPointSummary summary = userPointSummaryRepository.findByUserId(userId)
                .orElse(createDefaultSummary(userId));

        // 월이 바뀌었는지 확인하고 초기화
        String currentMonth = dateStr.substring(0, 6); // YYYYMM
        String lastProcessedMonth = summary.getLastProcessedMonth();

        System.out.println("DEBUG: " + userId + " 사용자의 월 확인 - 현재: " + currentMonth + ", 마지막 처리: " + lastProcessedMonth);

        if (lastProcessedMonth != null && !lastProcessedMonth.equals(currentMonth)) {
            // 새로운 월이 시작되었으므로 월간 넛지 건수만 초기화 (등급은 유지하지 않음)
            System.out
                    .println("DEBUG: " + userId + " 사용자의 월이 바뀜 - 이전: " + lastProcessedMonth + ", 현재: " + currentMonth);
            System.out.println("DEBUG: " + userId + " 사용자의 월별 데이터 초기화 시작");
            summary.resetMonthlyData();
            System.out.println("DEBUG: " + userId + " 사용자의 월별 데이터 초기화 완료");

            // 새로운 월의 등급과 월간 넛지 건수를 다시 계산
            // (현재 날짜의 넛지 건수 + 해당 월의 기존 넛지 건수)
            int newMonthlyNudgeCount = dailyNudgeCount; // 현재 날짜의 넛지 건수
            PointGrade newCurrentGrade = PointGrade.getGradeByNudgeCount(newMonthlyNudgeCount);

            System.out.println("DEBUG: " + userId + " 사용자의 새로운 월 데이터 - 넛지 건수: " + newMonthlyNudgeCount + ", 등급: "
                    + newCurrentGrade.getGradeName());

            // 총 보유 포인트에 금일 지급 포인트 추가
            summary.addPoints(totalPoints);

            // 새로운 월의 등급과 넛지 건수로 업데이트
            summary.updateGrade(newCurrentGrade.getGradeName());
            summary.updateMonthNudgeCount(newMonthlyNudgeCount);
        } else {
            System.out.println("DEBUG: " + userId + " 사용자의 월이 바뀌지 않음 - 초기화하지 않음");

            // 총 보유 포인트에 금일 지급 포인트 추가
            summary.addPoints(totalPoints);

            // 현재 등급을 이달 누적 넛지 건수 기준으로 업데이트
            summary.updateGrade(currentGrade.getGradeName());

            // 이달 누적 넛지 건수 업데이트
            summary.updateMonthNudgeCount(monthlyNudgeCount);
        }

        // 마지막 처리 월 업데이트
        summary.updateLastProcessedMonth(currentMonth);

        // 업데이트된 요약 정보 저장
        userPointSummaryRepository.save(summary);
        System.out.println("DEBUG: " + userId + " 사용자의 포인트 요약 정보 업데이트 완료 - 총 포인트: " + summary.getTotalPoints()
                + ", 등급: " + summary.getCurrentGrade());
    }

    /**
     * 신규 사용자를 위한 기본 포인트 요약 정보 생성
     * 
     * @param userId 사용자 ID
     * @return 기본값으로 초기화된 포인트 요약 정보
     */
    private TbUserPointSummary createDefaultSummary(String userId) {
        return TbUserPointSummary.builder()
                .userId(userId)
                .totalPoints(0) // 초기 포인트 0
                .currentGrade("bronze") // 초기 등급 Bronze
                .monthNudgeCount(0) // 초기 넛지 건수 0
                .lastProcessedMonth(null) // 마지막 처리 월 초기값
                .build();
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