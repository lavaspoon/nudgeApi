package devlava.nudgeapi.service;

import devlava.nudgeapi.dto.PointInfoDto;
import devlava.nudgeapi.entity.data.PointHistory;
import devlava.nudgeapi.repository.data.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(transactionManager = "dataTransactionManager")
public class PointService {

    private final PointHistoryRepository pointHistoryRepository;

    /**
     * 포인트 적립
     */
    public PointHistory earnPoints(String skid, int amount, String reason, String description) {
        int currentBalance = getCurrentBalance(skid);
        int newBalance = currentBalance + amount;

        PointHistory pointHistory = new PointHistory();
        pointHistory.setSkid(skid);
        pointHistory.setPointType("EARN");
        pointHistory.setPointAmount(amount);
        pointHistory.setPointReason(reason);
        pointHistory.setPointDescription(description);
        pointHistory.setBalanceAfter(newBalance);

        return pointHistoryRepository.save(pointHistory);
    }

    /**
     * 포인트 사용
     */
    public PointHistory usePoints(String skid, int amount, String reason, String description) {
        int currentBalance = getCurrentBalance(skid);

        if (currentBalance < amount) {
            throw new RuntimeException("포인트가 부족합니다.");
        }

        int newBalance = currentBalance - amount;

        PointHistory pointHistory = new PointHistory();
        pointHistory.setSkid(skid);
        pointHistory.setPointType("USE");
        pointHistory.setPointAmount(amount);
        pointHistory.setPointReason(reason);
        pointHistory.setPointDescription(description);
        pointHistory.setBalanceAfter(newBalance);

        return pointHistoryRepository.save(pointHistory);
    }

    /**
     * 현재 포인트 잔액 조회
     */
    public int getCurrentBalance(String skid) {
        Integer balance = pointHistoryRepository.findCurrentBalanceBySkid(skid);
        return balance != null ? balance : 0;
    }

    /**
     * 포인트 내역 조회
     */
    public List<PointHistory> getPointHistory(String skid) {
        return pointHistoryRepository.findBySkidOrderByCreatedAtDesc(skid);
    }

    /**
     * 포인트 타입별 내역 조회
     */
    public List<PointHistory> getPointHistoryByType(String skid, String pointType) {
        return pointHistoryRepository.findBySkidAndPointTypeOrderByCreatedAtDesc(skid, pointType);
    }

    /**
     * 총 적립 포인트 조회
     */
    public int getTotalEarnedPoints(String skid) {
        Integer totalEarned = pointHistoryRepository.findTotalEarnedPointsBySkid(skid);
        return totalEarned != null ? totalEarned : 0;
    }

    /**
     * 총 사용 포인트 조회
     */
    public int getTotalUsedPoints(String skid) {
        Integer totalUsed = pointHistoryRepository.findTotalUsedPointsBySkid(skid);
        return totalUsed != null ? totalUsed : 0;
    }

    /**
     * 통합 포인트 정보 조회
     */
    public PointInfoDto getPointInfo(String skid) {
        // 현재 포인트 정보
        int currentPoints = getCurrentBalance(skid);
        String currentGrade = getGradeByPoints(currentPoints);
        String nextGrade = getNextGradeByPoints(currentPoints);
        int gradeProgress = calculateGradeProgress(currentPoints, currentGrade, nextGrade);
        int weeklyEarned = calculateWeeklyEarnedPoints(skid);

        // 포인트 통계
        int totalEarned = getTotalEarnedPoints(skid);
        int totalUsed = getTotalUsedPoints(skid);

        // 포인트 내역
        List<PointInfoDto.PointHistoryDto> earnHistory = getPointHistoryByType(skid, "EARN")
                .stream()
                .limit(10) // 최근 10개만
                .map(this::convertToPointHistoryDto)
                .collect(Collectors.toList());

        List<PointInfoDto.PointHistoryDto> useHistory = getPointHistoryByType(skid, "USE")
                .stream()
                .limit(10) // 최근 10개만
                .map(this::convertToPointHistoryDto)
                .collect(Collectors.toList());

        return PointInfoDto.builder()
                .currentPoints(currentPoints)
                .currentGrade(currentGrade)
                .nextGrade(nextGrade)
                .gradeProgress(gradeProgress)
                .teamRank(3) // 실제로는 팀 순위 계산 로직 필요
                .weeklyEarned(weeklyEarned)
                .totalEarned(totalEarned)
                .totalUsed(totalUsed)
                .earnHistory(earnHistory)
                .useHistory(useHistory)
                .build();
    }

    /**
     * PointHistory를 PointHistoryDto로 변환
     */
    private PointInfoDto.PointHistoryDto convertToPointHistoryDto(PointHistory pointHistory) {
        return PointInfoDto.PointHistoryDto.builder()
                .id(pointHistory.getId())
                .pointType(pointHistory.getPointType())
                .pointAmount(pointHistory.getPointAmount())
                .pointReason(pointHistory.getPointReason())
                .pointDescription(pointHistory.getPointDescription())
                .balanceAfter(pointHistory.getBalanceAfter())
                .createdAt(pointHistory.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }

    /**
     * 넛지 성공 시 포인트 적립
     */
    public PointHistory earnPointsForNudgeSuccess(String skid) {
        return earnPoints(skid, 150, "넛지 성공 보너스", "고객이 넛지 제안에 동의하여 포인트를 적립받았습니다.");
    }

    /**
     * 고객 만족도 우수 시 포인트 적립
     */
    public PointHistory earnPointsForCustomerSatisfaction(String skid) {
        return earnPoints(skid, 100, "고객 만족도 우수", "고객 만족도가 우수하여 포인트를 적립받았습니다.");
    }

    /**
     * 일일 성과 달성 시 포인트 적립
     */
    public PointHistory earnPointsForDailyAchievement(String skid) {
        return earnPoints(skid, 50, "일일 성과 달성", "일일 목표를 달성하여 포인트를 적립받았습니다.");
    }

    /**
     * 주간 성과 1위 시 포인트 적립
     */
    public PointHistory earnPointsForWeeklyFirst(String skid) {
        return earnPoints(skid, 200, "주간 성과 1위", "주간 성과 1위를 달성하여 포인트를 적립받았습니다.");
    }

    /**
     * 월간 우수상담원 시 포인트 적립
     */
    public PointHistory earnPointsForMonthlyExcellence(String skid) {
        return earnPoints(skid, 300, "월간 우수상담원", "월간 우수상담원으로 선정되어 포인트를 적립받았습니다.");
    }

    /**
     * 포인트에 따른 등급 반환
     */
    private String getGradeByPoints(int points) {
        if (points >= 5000)
            return "플래티넘";
        if (points >= 2500)
            return "골드";
        if (points >= 1000)
            return "실버";
        return "브론즈";
    }

    /**
     * 다음 등급 반환
     */
    private String getNextGradeByPoints(int points) {
        if (points < 1000)
            return "실버";
        if (points < 2500)
            return "골드";
        if (points < 5000)
            return "플래티넘";
        return "최고 등급";
    }

    /**
     * 등급 진행률 계산
     */
    private int calculateGradeProgress(int points, String currentGrade, String nextGrade) {
        if ("최고 등급".equals(nextGrade))
            return 100;

        int currentMin = getGradeMinPoints(currentGrade);
        int nextMin = getGradeMinPoints(nextGrade);

        if (nextMin == currentMin)
            return 100;

        return (int) ((double) (points - currentMin) / (nextMin - currentMin) * 100);
    }

    /**
     * 등급별 최소 포인트 반환
     */
    private int getGradeMinPoints(String grade) {
        switch (grade) {
            case "브론즈":
                return 0;
            case "실버":
                return 1000;
            case "골드":
                return 2500;
            case "플래티넘":
                return 5000;
            default:
                return 0;
        }
    }

    /**
     * 이번주 적립 포인트 계산
     */
    private int calculateWeeklyEarnedPoints(String skid) {
        // 실제로는 이번주 데이터만 필터링해야 함
        List<PointHistory> weeklyHistory = pointHistoryRepository.findBySkidAndPointTypeOrderByCreatedAtDesc(skid,
                "EARN");
        return weeklyHistory.stream()
                .limit(10) // 임시로 최근 10개만 계산
                .mapToInt(PointHistory::getPointAmount)
                .sum();
    }
}