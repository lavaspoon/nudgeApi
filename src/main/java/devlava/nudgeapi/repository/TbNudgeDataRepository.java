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

        @Query("SELECT COUNT(n) FROM TbNudgeData n WHERE n.userId = :userId AND n.nudgeYn = 'Y' AND n.consultationDate LIKE :monthPrefix%")
        Integer countMonthlyNudgeByUserId(@Param("userId") String userId, @Param("monthPrefix") String monthPrefix);

        @Query("SELECT n.consultationDate, COUNT(n) FROM TbNudgeData n " +
                        "WHERE n.userId = :userId AND n.nudgeYn = 'Y' " +
                        "AND (n.consultationDate LIKE CONCAT(:lastMonthPrefix, '%') OR n.consultationDate LIKE CONCAT(:currentMonthPrefix, '%')) "
                        +
                        "GROUP BY n.consultationDate " +
                        "ORDER BY n.consultationDate")
        List<Object[]> getNudgeCountByUserIdAndMonths(@Param("userId") String userId,
                        @Param("lastMonthPrefix") String lastMonthPrefix,
                        @Param("currentMonthPrefix") String currentMonthPrefix);

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
}
