package devlava.nudgeapi.service;

import devlava.nudgeapi.entity.data.NudgeConsultation;
import devlava.nudgeapi.entity.data.PointHistory;
import devlava.nudgeapi.repository.data.NudgeConsultationRepository;
import devlava.nudgeapi.repository.data.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(transactionManager = "dataTransactionManager")
public class NudgePointSchedulerService {

    private final NudgeConsultationRepository nudgeConsultationRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final PointService pointService;

    private static final int POINTS_PER_NUDGE = 50; // 넛지 건당 적립 포인트

    /**
     * 새벽 2시에 실행되는 스케줄러
     * 어제 넛지 성공 건수에 따라 포인트 적립
     */
    @Scheduled(cron = "0 0 2 * * ?") // 매일 새벽 2시
    public void processNudgePoints() {
        log.info("넛지 포인트 적립 스케줄러 시작");

        try {
            // 어제 날짜 계산
            String yesterday = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            log.info("처리 대상 날짜: {}", yesterday);

            // 중복 적립 방지를 위해 이미 처리된 날짜인지 확인
            if (isAlreadyProcessed(yesterday)) {
                log.info("{} 날짜는 이미 처리되었습니다.", yesterday);
                return;
            }

            // 전체 구성원의 넛지 성공 건수 조회 (한 번의 쿼리로 모든 데이터 조회)
            List<Object[]> nudgeCounts = nudgeConsultationRepository.findNudgeSuccessCountByDateGroupBySkid(yesterday);

            if (nudgeCounts.isEmpty()) {
                log.info("어제 넛지 성공 데이터가 없습니다.");
                // 빈 데이터도 처리 완료로 기록
                markAsProcessed(yesterday);
                return;
            }

            // 배치 처리를 위한 포인트 내역 리스트
            List<PointHistory> batchPointHistory = new ArrayList<>();
            int totalProcessed = 0;
            int totalPointsEarned = 0;

            for (Object[] result : nudgeCounts) {
                String skid = (String) result[0];
                Long count = (Long) result[1];

                if (count > 0) {
                    int pointsToEarn = count.intValue() * POINTS_PER_NUDGE;
                    int currentBalance = pointService.getCurrentBalance(skid);
                    int newBalance = currentBalance + pointsToEarn;

                    // 포인트 내역 객체 생성 (아직 저장하지 않음)
                    PointHistory pointHistory = new PointHistory();
                    pointHistory.setSkid(skid);
                    pointHistory.setPointType("EARN");
                    pointHistory.setPointAmount(pointsToEarn);
                    pointHistory.setPointReason("넛지 통합 성공 보너스");
                    pointHistory.setPointDescription(String.format("어제(%s) 넛지 성공 %d건에 대한 포인트 적립", yesterday, count));
                    pointHistory.setBalanceAfter(newBalance);

                    batchPointHistory.add(pointHistory);
                    totalProcessed++;
                    totalPointsEarned += pointsToEarn;

                    log.info("사번: {}, 넛지 성공: {}건, 적립 포인트: {}P", skid, count, pointsToEarn);
                }
            }

            // 배치로 한 번에 저장 (300명이면 1번의 배치 쿼리)
            if (!batchPointHistory.isEmpty()) {
                pointHistoryRepository.saveAll(batchPointHistory);
                log.info("배치 포인트 적립 완료 - {}개의 포인트 내역을 한 번에 저장", batchPointHistory.size());
            }

            // 처리 완료 표시
            markAsProcessed(yesterday);

            log.info("넛지 포인트 적립 완료 - 처리된 사용자: {}명, 총 적립 포인트: {}P",
                    totalProcessed, totalPointsEarned);

        } catch (Exception e) {
            log.error("넛지 포인트 적립 스케줄러 실행 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    /**
     * 수동으로 특정 날짜의 넛지 포인트 적립 처리
     */
    public void processNudgePointsForDate(String date) {
        log.info("수동 넛지 포인트 적립 시작 - 날짜: {}", date);

        try {
            // 중복 적립 방지를 위해 이미 처리된 날짜인지 확인
            if (isAlreadyProcessed(date)) {
                log.info("{} 날짜는 이미 처리되었습니다.", date);
                return;
            }

            List<Object[]> nudgeCounts = nudgeConsultationRepository.findNudgeSuccessCountByDateGroupBySkid(date);

            if (nudgeCounts.isEmpty()) {
                log.info("해당 날짜({})의 넛지 성공 데이터가 없습니다.", date);
                // 빈 데이터도 처리 완료로 기록
                markAsProcessed(date);
                return;
            }

            // 배치 처리를 위한 포인트 내역 리스트
            List<PointHistory> batchPointHistory = new ArrayList<>();
            int totalProcessed = 0;
            int totalPointsEarned = 0;

            for (Object[] result : nudgeCounts) {
                String skid = (String) result[0];
                Long count = (Long) result[1];

                if (count > 0) {
                    int pointsToEarn = count.intValue() * POINTS_PER_NUDGE;
                    int currentBalance = pointService.getCurrentBalance(skid);
                    int newBalance = currentBalance + pointsToEarn;

                    // 포인트 내역 객체 생성 (아직 저장하지 않음)
                    PointHistory pointHistory = new PointHistory();
                    pointHistory.setSkid(skid);
                    pointHistory.setPointType("EARN");
                    pointHistory.setPointAmount(pointsToEarn);
                    pointHistory.setPointReason("넛지 통합 성공 보너스");
                    pointHistory.setPointDescription(String.format("%s 넛지 성공 %d건에 대한 포인트 적립", date, count));
                    pointHistory.setBalanceAfter(newBalance);

                    batchPointHistory.add(pointHistory);
                    totalProcessed++;
                    totalPointsEarned += pointsToEarn;

                    log.info("사번: {}, 넛지 성공: {}건, 적립 포인트: {}P", skid, count, pointsToEarn);
                }
            }

            // 배치로 한 번에 저장
            if (!batchPointHistory.isEmpty()) {
                pointHistoryRepository.saveAll(batchPointHistory);
                log.info("배치 포인트 적립 완료 - {}개의 포인트 내역을 한 번에 저장", batchPointHistory.size());
            }

            // 처리 완료 표시
            markAsProcessed(date);

            log.info("수동 넛지 포인트 적립 완료 - 처리된 사용자: {}명, 총 적립 포인트: {}P",
                    totalProcessed, totalPointsEarned);

        } catch (Exception e) {
            log.error("수동 넛지 포인트 적립 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    /**
     * 특정 사용자의 특정 날짜 넛지 성공 건수 조회
     */
    public Long getNudgeSuccessCount(String skid, String date) {
        return nudgeConsultationRepository.countNudgeSuccessBySkidAndDate(skid, date);
    }

    /**
     * 전체 구성원 목록 조회
     */
    public List<String> getAllSkids() {
        return nudgeConsultationRepository.findAllSkids();
    }

    /**
     * 특정 날짜가 이미 처리되었는지 확인
     */
    private boolean isAlreadyProcessed(String date) {
        // 처리 완료 표시를 위한 테이블이나 플래그를 확인
        // 여기서는 간단히 해당 날짜의 포인트 내역이 있는지 확인
        List<PointHistory> existingHistory = pointHistoryRepository
                .findBySkidAndPointTypeOrderByCreatedAtDesc("SYSTEM");

        // SYSTEM 사용자로 처리 완료 표시를 저장하는 방식
        return existingHistory.stream()
                .anyMatch(history -> history.getPointReason().contains(date) &&
                        history.getPointReason().contains("처리완료"));
    }

    /**
     * 특정 날짜를 처리 완료로 표시
     */
    private void markAsProcessed(String date) {
        PointHistory processedFlag = new PointHistory();
        processedFlag.setSkid("SYSTEM");
        processedFlag.setPointType("EARN");
        processedFlag.setPointAmount(0);
        processedFlag.setPointReason("넛지 포인트 적립 처리완료 - " + date);
        processedFlag.setPointDescription("배치 처리 완료 표시");
        processedFlag.setBalanceAfter(0);

        pointHistoryRepository.save(processedFlag);
    }
}