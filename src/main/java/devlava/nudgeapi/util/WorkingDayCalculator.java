package devlava.nudgeapi.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;

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
}
