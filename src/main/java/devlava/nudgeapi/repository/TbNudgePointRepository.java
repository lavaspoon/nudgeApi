package devlava.nudgeapi.repository;

import devlava.nudgeapi.entity.TbNudgePoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TbNudgePointRepository extends JpaRepository<TbNudgePoint, Long> {
    List<TbNudgePoint> findByUserIdOrderByCreatedDateDesc(String userId);

    /**
     * 특정 사용자의 이번달 총 포인트 조회
     */
    @Query("SELECT COALESCE(SUM(np.pointAmount), 0) FROM TbNudgePoint np " +
            "WHERE np.userId = :userId AND YEAR(np.createdDate) = YEAR(CURRENT_DATE) " +
            "AND MONTH(np.createdDate) = MONTH(CURRENT_DATE)")
    Integer findTotalPointsByUserIdThisMonth(@Param("userId") String userId);

    /**
     * 여러 사용자의 이번달 총 포인트 배치 조회
     */
    @Query("SELECT np.userId, COALESCE(SUM(np.pointAmount), 0) FROM TbNudgePoint np " +
            "WHERE np.userId IN :userIds AND YEAR(np.createdDate) = YEAR(CURRENT_DATE) " +
            "AND MONTH(np.createdDate) = MONTH(CURRENT_DATE) " +
            "GROUP BY np.userId")
    List<Object[]> findTotalPointsByUserIdsThisMonth(@Param("userIds") List<String> userIds);
}
