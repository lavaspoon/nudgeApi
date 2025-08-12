package devlava.nudgeapi.service;

import devlava.nudgeapi.dto.ChartDto;
import devlava.nudgeapi.repository.TbNudgeDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChartService {

    private final TbNudgeDataRepository tbNudgeDataRepository;

    /**
     * 특정 사용자의 전월과 이번달 영업일별 nudgeYn 건수를 조회
     */
    public ChartDto getMonthlyNudgeCountByBusinessDays(String userId) {
        LocalDate now = LocalDate.now();
        LocalDate currentMonth = now.withDayOfMonth(1);
        LocalDate lastMonth = currentMonth.minusMonths(1);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
        String lastMonthPrefix = lastMonth.format(formatter);
        String currentMonthPrefix = currentMonth.format(formatter);

        log.info("조회 조건 - userId: {}, lastMonth: {}, currentMonth: {}",
                userId, lastMonthPrefix, currentMonthPrefix);

        // 한 번의 쿼리로 전월과 이번달 데이터 모두 조회
        List<Object[]> nudgeData = tbNudgeDataRepository.getNudgeCountByUserIdAndMonths(
                userId, lastMonthPrefix, currentMonthPrefix);

        log.info("조회된 raw 데이터 건수: {}", nudgeData.size());

        // 데이터를 날짜별로 분류
        Map<String, Integer> lastMonthNudgeCount = new LinkedHashMap<>();
        Map<String, Integer> currentMonthNudgeCount = new LinkedHashMap<>();

        for (Object[] data : nudgeData) {
            String consultationDate = (String) data[0];
            Integer count = ((Number) data[1]).intValue();

            // consultationDate가 8자리 이상인지 확인
            if (consultationDate != null && consultationDate.length() >= 8) {
                String dateKey = consultationDate.substring(0, 8); // yyyyMMdd 형식

                try {
                    LocalDate date = LocalDate.parse(dateKey, DateTimeFormatter.ofPattern("yyyyMMdd"));

                    // 영업일인지 확인
                    if (isBusinessDay(date)) {
                        if (consultationDate.startsWith(lastMonthPrefix)) {
                            lastMonthNudgeCount.put(dateKey, count);
                        } else if (consultationDate.startsWith(currentMonthPrefix)) {
                            currentMonthNudgeCount.put(dateKey, count);
                        }
                    }
                } catch (Exception e) {
                    log.warn("날짜 파싱 오류 - dateKey: {}, error: {}", dateKey, e.getMessage());
                }
            }
        }

        // 영업일별로 0건인 날짜도 포함하여 완전한 데이터 생성
        lastMonthNudgeCount = fillBusinessDays(lastMonthNudgeCount, lastMonth);
        currentMonthNudgeCount = fillBusinessDays(currentMonthNudgeCount, currentMonth);

        // 총합 계산
        int lastMonthTotal = lastMonthNudgeCount.values().stream().mapToInt(Integer::intValue).sum();
        int currentMonthTotal = currentMonthNudgeCount.values().stream().mapToInt(Integer::intValue).sum();

        log.info("결과 - 전월 영업일 수: {}, 전월 총 건수: {}, 이번달 영업일 수: {}, 이번달 총 건수: {}",
                lastMonthNudgeCount.size(), lastMonthTotal,
                currentMonthNudgeCount.size(), currentMonthTotal);

        return ChartDto.builder()
                .lastMonthNudgeCount(lastMonthNudgeCount)
                .currentMonthNudgeCount(currentMonthNudgeCount)
                .lastMonthTotal(lastMonthTotal)
                .currentMonthTotal(currentMonthTotal)
                .build();
    }

    /**
     * 해당 월의 모든 영업일을 포함하여 데이터 생성 (0건인 날짜도 포함)
     */
    private Map<String, Integer> fillBusinessDays(Map<String, Integer> existingData, LocalDate monthStart) {
        Map<String, Integer> completeData = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        // 해당 월의 마지막 날 구하기
        LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth());

        // 해당 월의 모든 영업일을 순회
        LocalDate currentDate = monthStart;
        while (!currentDate.isAfter(monthEnd)) {
            if (isBusinessDay(currentDate)) {
                String dateKey = currentDate.format(formatter);
                completeData.put(dateKey, existingData.getOrDefault(dateKey, 0));
            }
            currentDate = currentDate.plusDays(1);
        }

        return completeData;
    }

    /**
     * 영업일 여부 확인 (토요일, 일요일 제외)
     * 추후 공휴일 처리도 추가 가능
     */
    private boolean isBusinessDay(LocalDate date) {
        int dayOfWeek = date.getDayOfWeek().getValue();
        boolean isWeekday = dayOfWeek >= 1 && dayOfWeek <= 5;
        return isWeekday;
    }
}