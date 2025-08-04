package devlava.nudgeapi.repository.data;

import devlava.nudgeapi.entity.data.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {

    // 특정 사용자의 포인트 내역 조회
    List<PointHistory> findBySkidOrderByCreatedAtDesc(String skid);

    // 특정 사용자의 포인트 타입별 내역 조회
    List<PointHistory> findBySkidAndPointTypeOrderByCreatedAtDesc(String skid, String pointType);

    // 특정 사용자의 현재 포인트 잔액 조회
    @Query("SELECT ph.balanceAfter FROM PointHistory ph WHERE ph.skid = :skid ORDER BY ph.createdAt DESC LIMIT 1")
    Integer findCurrentBalanceBySkid(@Param("skid") String skid);

    // 특정 사용자의 총 적립 포인트
    @Query("SELECT COALESCE(SUM(ph.pointAmount), 0) FROM PointHistory ph WHERE ph.skid = :skid AND ph.pointType = 'EARN'")
    Integer findTotalEarnedPointsBySkid(@Param("skid") String skid);

    // 특정 사용자의 총 사용 포인트
    @Query("SELECT COALESCE(SUM(ph.pointAmount), 0) FROM PointHistory ph WHERE ph.skid = :skid AND ph.pointType = 'USE'")
    Integer findTotalUsedPointsBySkid(@Param("skid") String skid);
} 