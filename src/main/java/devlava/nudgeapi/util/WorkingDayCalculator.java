package devlava.nudgeapi.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * 영업일 계산 유틸리티 클래스
 */
public class WorkingDayCalculator {

    /**
     * 특정 년월의 영업일 수 계산 (주말 제외)
     * 
     * @param yearMonth 계산할 년월
     * @return 영업일 수
     */
    public static int calculateWorkingDays(YearMonth yearMonth) {
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        int workingDays = 0;
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            if (currentDate.getDayOfWeek() != DayOfWeek.SATURDAY &&
                    currentDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
                workingDays++;
            }
            currentDate = currentDate.plusDays(1);
        }

        return workingDays;
    }

    /**
     * 현재 월의 영업일 수 계산
     * 
     * @return 현재 월의 영업일 수
     */
    public static int calculateCurrentMonthWorkingDays() {
        return calculateWorkingDays(YearMonth.now());
    }

    /**
     * 어제부터 영업일 기준 최근 5일 계산
     * 
     * @return 최근 5일 영업일 목록 (yyyyMMdd 형식)
     */
    public static List<String> getRecent5WorkingDays() {
        List<String> workingDays = new ArrayList<>();
        LocalDate currentDate = LocalDate.now().minusDays(1); // 어제부터 시작
        int count = 0;

        while (workingDays.size() < 5) {
            if (currentDate.getDayOfWeek() != DayOfWeek.SATURDAY &&
                    currentDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
                workingDays.add(currentDate.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")));
            }
            currentDate = currentDate.minusDays(1);

            // 무한 루프 방지 (최대 30일 전까지)
            if (count++ > 30) {
                break;
            }
        }

        return workingDays;
    }

    /**
     * 테스트용: 2025년 8월 영업일 기준 최근 5일 계산
     * 
     * @return 최근 5일 영업일 목록 (yyyyMMdd 형식)
     */
    public static List<String> getTestRecent5WorkingDays() {
        List<String> workingDays = new ArrayList<>();
        LocalDate currentDate = LocalDate.of(2025, 8, 11); // 2025년 8월 11일부터 시작
        int count = 0;

        while (workingDays.size() < 5) {
            if (currentDate.getDayOfWeek() != DayOfWeek.SATURDAY &&
                    currentDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
                workingDays.add(currentDate.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")));
            }
            currentDate = currentDate.minusDays(1);

            // 무한 루프 방지 (최대 30일 전까지)
            if (count++ > 30) {
                break;
            }
        }

        return workingDays;
    }
}
