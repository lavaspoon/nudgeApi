package devlava.nudgeapi.repository;

import devlava.nudgeapi.entity.TbNudgeData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TbNudgeDataRepository extends JpaRepository<TbNudgeData, Long> {

        @Query("SELECT COUNT(n) FROM TbNudgeData n WHERE n.userId = :userId AND n.nudgeYn = 'Y' AND n.consultationDate LIKE :datePrefix%")
        Integer countNudgeByUserIdAndDate(@Param("userId") String userId, @Param("datePrefix") String datePrefix);

        /**
         * 특정 사용자의 특정 날짜 넛지 성공 건수 조회
         */
        @Query("SELECT COUNT(n) FROM TbNudgeData n WHERE n.userId = :userId AND n.nudgeYn = 'Y' AND n.customerConsentYn = 'Y' AND n.consultationDate LIKE :datePrefix%")
        Integer countNudgeSuccessByUserIdAndDate(@Param("userId") String userId,
                        @Param("datePrefix") String datePrefix);

        @Query("SELECT COUNT(n) FROM TbNudgeData n WHERE n.userId = :userId AND n.nudgeYn = 'Y' AND n.consultationDate LIKE :monthPrefix%")
        Integer countMonthlyNudgeByUserId(@Param("userId") String userId, @Param("monthPrefix") String monthPrefix);

        @Query("SELECT SUBSTRING(n.consultationDate, 1, 8), COUNT(n) FROM TbNudgeData n " +
                        "WHERE n.userId = :userId AND n.nudgeYn = 'Y' " +
                        "AND (n.consultationDate LIKE CONCAT(:lastMonthPrefix, '%') OR n.consultationDate LIKE CONCAT(:currentMonthPrefix, '%')) "
                        +
                        "GROUP BY SUBSTRING(n.consultationDate, 1, 8) " +
                        "ORDER BY SUBSTRING(n.consultationDate, 1, 8)")
        List<Object[]> getNudgeCountByUserIdAndMonths(@Param("userId") String userId,
                        @Param("lastMonthPrefix") String lastMonthPrefix,
                        @Param("currentMonthPrefix") String currentMonthPrefix);

        /**
         * 특정 사용자의 특정 날짜 nudgeYn 건수 조회
         */
        @Query("SELECT SUBSTRING(n.consultationDate, 1, 8), COUNT(n) FROM TbNudgeData n " +
                        "WHERE n.userId = :userId AND n.nudgeYn = 'Y' " +
                        "AND n.consultationDate LIKE CONCAT(:datePrefix, '%') " +
                        "GROUP BY SUBSTRING(n.consultationDate, 1, 8) " +
                        "ORDER BY SUBSTRING(n.consultationDate, 1, 8)")
        List<Object[]> getNudgeCountByUserIdAndDate(@Param("userId") String userId,
                        @Param("datePrefix") String datePrefix);

        /**
         * 이번달 기준으로 nudgeYn이 가장 많은 사용자 1위, 2위 조회
         */
        @Query("SELECT n.userId, COUNT(n) as nudgeCount " +
                        "FROM TbNudgeData n " +
                        "WHERE n.nudgeYn = 'Y' AND n.consultationDate LIKE :monthPrefix% " +
                        "GROUP BY n.userId " +
                        "ORDER BY nudgeCount DESC " +
                        "LIMIT 2")
        List<Object[]> findTopNudgeUsersByMonth(@Param("monthPrefix") String monthPrefix);

        /**
         * 특정 사용자의 최근 넛지 데이터 조회 (멘트 포함)
         */
        @Query("SELECT n FROM TbNudgeData n " +
                        "WHERE n.userId = :userId AND n.nudgeYn = 'Y' " +
                        "ORDER BY n.consultationDate DESC, n.id DESC " +
                        "LIMIT 1")
        TbNudgeData findLatestNudgeByUserId(@Param("userId") String userId);

        /**
         * 특정 사용자 목록의 이번달 넛지 통계 조회
         */
        @Query("SELECT n.userId, COUNT(n) as totalCount, " +
                        "SUM(CASE WHEN n.customerConsentYn = 'Y' THEN 1 ELSE 0 END) as successCount " +
                        "FROM TbNudgeData n " +
                        "WHERE n.userId IN :userIds AND n.nudgeYn = 'Y' AND n.consultationDate LIKE :monthPrefix% " +
                        "GROUP BY n.userId")
        List<Object[]> findNudgeStatsByUserIds(@Param("userIds") List<String> userIds,
                        @Param("monthPrefix") String monthPrefix);

        /**
         * 전체 사용자 중 이번달 1위 넛지 사용자 조회
         */
        @Query("SELECT n.userId, COUNT(n) as totalCount, " +
                        "SUM(CASE WHEN n.customerConsentYn = 'Y' THEN 1 ELSE 0 END) as successCount " +
                        "FROM TbNudgeData n " +
                        "WHERE n.nudgeYn = 'Y' AND n.consultationDate LIKE :monthPrefix% " +
                        "GROUP BY n.userId " +
                        "ORDER BY totalCount DESC " +
                        "LIMIT 1")
        List<Object[]> findTopNudgeUserByMonth(@Param("monthPrefix") String monthPrefix);

        /**
         * 전체 사용자 중 이번달 상위 5명 넛지 사용자 조회
         */
        @Query("SELECT n.userId, COUNT(n) as totalCount, " +
                        "SUM(CASE WHEN n.customerConsentYn = 'Y' THEN 1 ELSE 0 END) as successCount " +
                        "FROM TbNudgeData n " +
                        "WHERE n.nudgeYn = 'Y' AND n.consultationDate LIKE :monthPrefix% " +
                        "GROUP BY n.userId " +
                        "ORDER BY totalCount DESC " +
                        "LIMIT 5")
        List<Object[]> findTop5NudgeUsersByMonth(@Param("monthPrefix") String monthPrefix);

        /**
         * 사용자별 이번달 상세 통계 조회 (넛지율, GIGA, CRM, TDS)
         */
        @Query("SELECT n.userId, " +
                        "COUNT(n) as totalCount, " +
                        "SUM(CASE WHEN n.nudgeYn = 'Y' THEN 1 ELSE 0 END) as nudgeCount, " +
                        "SUM(CASE WHEN n.marketingType LIKE 'GIGA%' THEN 1 ELSE 0 END) as gigaCount, " +
                        "SUM(CASE WHEN n.marketingType LIKE 'CRM%' THEN 1 ELSE 0 END) as crmCount, " +
                        "SUM(CASE WHEN n.marketingType LIKE 'TDS%' THEN 1 ELSE 0 END) as tdsCount " +
                        "FROM TbNudgeData n " +
                        "WHERE n.userId IN :userIds AND n.consultationDate LIKE :monthPrefix% " +
                        "GROUP BY n.userId")
        List<Object[]> findUserDetailedStatsByMonth(@Param("userIds") List<String> userIds,
                        @Param("monthPrefix") String monthPrefix);

        /**
         * 사용자별 전일 넛지 통계 조회
         */
        @Query("SELECT n.userId, " +
                        "COUNT(n) as totalCount, " +
                        "SUM(CASE WHEN n.nudgeYn = 'Y' THEN 1 ELSE 0 END) as nudgeCount " +
                        "FROM TbNudgeData n " +
                        "WHERE n.userId IN :userIds AND n.consultationDate = :prevDay " +
                        "GROUP BY n.userId")
        List<Object[]> findUserPrevDayStats(@Param("userIds") List<String> userIds,
                        @Param("prevDay") String prevDay);

        /**
         * 넛지 건수 상위 5위 조회
         */
        @Query("SELECT n.userId, COUNT(n) as nudgeCount " +
                        "FROM TbNudgeData n " +
                        "WHERE n.nudgeYn = 'Y' AND n.consultationDate LIKE :monthPrefix% " +
                        "GROUP BY n.userId " +
                        "ORDER BY nudgeCount DESC " +
                        "LIMIT 5")
        List<Object[]> findTop5NudgeCountByMonth(@Param("monthPrefix") String monthPrefix);

        /**
         * GIGA 건수 상위 5위 조회
         */
        @Query("SELECT n.userId, COUNT(n) as gigaCount " +
                        "FROM TbNudgeData n " +
                        "WHERE n.marketingType LIKE 'GIGA%' AND n.consultationDate LIKE :monthPrefix% " +
                        "GROUP BY n.userId " +
                        "ORDER BY gigaCount DESC " +
                        "LIMIT 5")
        List<Object[]> findTop5GigaCountByMonth(@Param("monthPrefix") String monthPrefix);

        /**
         * TDS 건수 상위 5위 조회
         */
        @Query("SELECT n.userId, COUNT(n) as tdsCount " +
                        "FROM TbNudgeData n " +
                        "WHERE n.marketingType LIKE 'TDS%' AND n.consultationDate LIKE :monthPrefix% " +
                        "GROUP BY n.userId " +
                        "ORDER BY tdsCount DESC " +
                        "LIMIT 5")
        List<Object[]> findTop5TdsCountByMonth(@Param("monthPrefix") String monthPrefix);

        /**
         * CRM 건수 상위 5위 조회
         */
        @Query("SELECT n.userId, COUNT(n) as crmCount " +
                        "FROM TbNudgeData n " +
                        "WHERE n.marketingType LIKE 'CRM%' AND n.consultationDate LIKE :monthPrefix% " +
                        "GROUP BY n.userId " +
                        "ORDER BY crmCount DESC " +
                        "LIMIT 5")
        List<Object[]> findTop5CrmCountByMonth(@Param("monthPrefix") String monthPrefix);

        /**
         * 특정 사용자의 최근 5일 영업일 데이터 조회
         */
        @Query("SELECT n FROM TbNudgeData n " +
                        "WHERE n.userId = :userId " +
                        "AND (n.consultationDate LIKE :workingDay1 || '%' OR " +
                        "     n.consultationDate LIKE :workingDay2 || '%' OR " +
                        "     n.consultationDate LIKE :workingDay3 || '%' OR " +
                        "     n.consultationDate LIKE :workingDay4 || '%' OR " +
                        "     n.consultationDate LIKE :workingDay5 || '%') " +
                        "ORDER BY n.consultationDate DESC, n.id DESC")
        List<TbNudgeData> findUserDataByWorkingDays(@Param("userId") String userId,
                        @Param("workingDay1") String workingDay1,
                        @Param("workingDay2") String workingDay2,
                        @Param("workingDay3") String workingDay3,
                        @Param("workingDay4") String workingDay4,
                        @Param("workingDay5") String workingDay5);

        /**
         * 특정 사용자의 특정 날짜별 통계 조회
         */
        @Query("SELECT SUBSTRING(n.consultationDate, 1, 8) as date, " +
                        "COUNT(n) as totalCount, " +
                        "SUM(CASE WHEN n.nudgeYn = 'Y' THEN 1 ELSE 0 END) as nudgeCount, " +
                        "SUM(CASE WHEN n.marketingType LIKE 'GIGA%' THEN 1 ELSE 0 END) as gigaCount, " +
                        "SUM(CASE WHEN n.marketingType LIKE 'CRM%' THEN 1 ELSE 0 END) as crmCount, " +
                        "SUM(CASE WHEN n.marketingType LIKE 'TDS%' THEN 1 ELSE 0 END) as tdsCount " +
                        "FROM TbNudgeData n " +
                        "WHERE n.userId = :userId " +
                        "AND (n.consultationDate LIKE :workingDay1 || '%' OR " +
                        "     n.consultationDate LIKE :workingDay2 || '%' OR " +
                        "     n.consultationDate LIKE :workingDay3 || '%' OR " +
                        "     n.consultationDate LIKE :workingDay4 || '%' OR " +
                        "     n.consultationDate LIKE :workingDay5 || '%') " +
                        "GROUP BY SUBSTRING(n.consultationDate, 1, 8) " +
                        "ORDER BY SUBSTRING(n.consultationDate, 1, 8) DESC")
        List<Object[]> findUserDailyStatsByWorkingDays(@Param("userId") String userId,
                        @Param("workingDay1") String workingDay1,
                        @Param("workingDay2") String workingDay2,
                        @Param("workingDay3") String workingDay3,
                        @Param("workingDay4") String workingDay4,
                        @Param("workingDay5") String workingDay5);

        /**
         * 부서별 이번달 일자별 넛지 통계 조회
         */
        @Query("SELECT m.deptIdx, m.deptName, " +
                        "SUBSTRING(n.consultationDate, 1, 8) as date, " +
                        "COUNT(n) as totalCount, " +
                        "SUM(CASE WHEN n.nudgeYn = 'Y' THEN 1 ELSE 0 END) as nudgeCount, " +
                        "SUM(CASE WHEN n.marketingType LIKE 'GIGA%' THEN 1 ELSE 0 END) as gigaCount, " +
                        "SUM(CASE WHEN n.marketingType LIKE 'CRM%' THEN 1 ELSE 0 END) as crmCount, " +
                        "SUM(CASE WHEN n.marketingType LIKE 'TDS%' THEN 1 ELSE 0 END) as tdsCount " +
                        "FROM TbNudgeData n " +
                        "JOIN TbLmsMember m ON n.userId = m.userId " +
                        "WHERE m.deptIdx IN :deptIds " +
                        "AND n.consultationDate LIKE :monthPrefix || '%' " +
                        "GROUP BY m.deptIdx, m.deptName, SUBSTRING(n.consultationDate, 1, 8) " +
                        "ORDER BY m.deptIdx, SUBSTRING(n.consultationDate, 1, 8)")
        List<Object[]> findDeptDailyStatsByMonth(@Param("deptIds") List<Integer> deptIds,
                        @Param("monthPrefix") String monthPrefix);

        /**
         * 부서별 월별 넛지 통계 조회 (최근 6개월)
         */
        @Query("SELECT m.deptIdx, m.deptName, " +
                        "SUBSTRING(n.consultationDate, 1, 6) as month, " +
                        "COUNT(n) as totalCount, " +
                        "SUM(CASE WHEN n.nudgeYn = 'Y' THEN 1 ELSE 0 END) as nudgeCount, " +
                        "SUM(CASE WHEN n.customerConsentYn = 'Y' THEN 1 ELSE 0 END) as successCount, " +
                        "SUM(CASE WHEN n.marketingType LIKE 'GIGA%' THEN 1 ELSE 0 END) as gigaCount, " +
                        "SUM(CASE WHEN n.marketingType LIKE 'CRM%' THEN 1 ELSE 0 END) as crmCount, " +
                        "SUM(CASE WHEN n.marketingType LIKE 'TDS%' THEN 1 ELSE 0 END) as tdsCount " +
                        "FROM TbNudgeData n " +
                        "JOIN TbLmsMember m ON n.userId = m.userId " +
                        "WHERE m.deptIdx IN :deptIds " +
                        "AND n.consultationDate LIKE CONCAT(:month, '%') " +
                        "GROUP BY m.deptIdx, m.deptName, SUBSTRING(n.consultationDate, 1, 6) " +
                        "ORDER BY m.deptIdx, SUBSTRING(n.consultationDate, 1, 6)")
        List<Object[]> findDeptMonthlyStatsByMonth(@Param("deptIds") List<Integer> deptIds,
                        @Param("month") String month);

        /**
         * 부서별 월별 넛지 통계 조회 (여러 월)
         */
        @Query("SELECT m.deptIdx, m.deptName, " +
                        "SUBSTRING(n.consultationDate, 1, 6) as month, " +
                        "COUNT(n) as totalCount, " +
                        "SUM(CASE WHEN n.nudgeYn = 'Y' THEN 1 ELSE 0 END) as nudgeCount, " +
                        "SUM(CASE WHEN n.customerConsentYn = 'Y' THEN 1 ELSE 0 END) as successCount, " +
                        "SUM(CASE WHEN n.marketingType LIKE 'GIGA%' THEN 1 ELSE 0 END) as gigaCount, " +
                        "SUM(CASE WHEN n.marketingType LIKE 'CRM%' THEN 1 ELSE 0 END) as crmCount, " +
                        "SUM(CASE WHEN n.marketingType LIKE 'TDS%' THEN 1 ELSE 0 END) as tdsCount " +
                        "FROM TbNudgeData n " +
                        "JOIN TbLmsMember m ON n.userId = m.userId " +
                        "WHERE m.deptIdx IN :deptIds " +
                        "AND SUBSTRING(n.consultationDate, 1, 6) IN :months " +
                        "GROUP BY m.deptIdx, m.deptName, SUBSTRING(n.consultationDate, 1, 6) " +
                        "ORDER BY m.deptIdx, SUBSTRING(n.consultationDate, 1, 6)")
        List<Object[]> findDeptMonthlyStatsByMonths(@Param("deptIds") List<Integer> deptIds,
                        @Param("months") List<String> months);

        /**
         * 특정 부서들의 이번달 넛지 건수 순위 조회
         */
        @Query("SELECT n.userId, COUNT(n) as nudgeCount " +
                        "FROM TbNudgeData n " +
                        "JOIN TbLmsMember m ON n.userId = m.userId " +
                        "WHERE m.deptIdx IN :deptIds " +
                        "AND n.nudgeYn = 'Y' " +
                        "AND n.consultationDate LIKE :monthPrefix% " +
                        "GROUP BY n.userId " +
                        "ORDER BY nudgeCount DESC")
        List<Object[]> findNudgeRankingByDeptIds(@Param("deptIds") List<Integer> deptIds,
                        @Param("monthPrefix") String monthPrefix);

        /**
         * 특정 사용자의 이번달 넛지 건수 조회
         */
        @Query("SELECT COUNT(n) FROM TbNudgeData n " +
                        "WHERE n.userId = :userId " +
                        "AND n.nudgeYn = 'Y' " +
                        "AND n.consultationDate LIKE :monthPrefix%")
        Integer countUserMonthlyNudge(@Param("userId") String userId,
                        @Param("monthPrefix") String monthPrefix);

        /**
         * 부서별 월별 넛지 통계 조회 (전월 대비 비교용)
         */
        @Query("SELECT m.deptIdx, m.deptName, " +
                        "COUNT(n) as totalCount, " +
                        "SUM(CASE WHEN n.nudgeYn = 'Y' THEN 1 ELSE 0 END) as nudgeCount, " +
                        "SUM(CASE WHEN n.customerConsentYn = 'Y' THEN 1 ELSE 0 END) as successCount " +
                        "FROM TbNudgeData n " +
                        "JOIN TbLmsMember m ON n.userId = m.userId " +
                        "WHERE m.deptIdx IN :deptIds " +
                        "AND n.consultationDate LIKE :monthPrefix% " +
                        "GROUP BY m.deptIdx, m.deptName")
        List<Object[]> findDeptMonthlyComparisonStats(@Param("deptIds") List<Integer> deptIds,
                        @Param("monthPrefix") String monthPrefix);
}
