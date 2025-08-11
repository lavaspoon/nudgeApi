package devlava.nudgeapi.repository;

import devlava.nudgeapi.entity.TbNudgePoint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TbNudgePointRepository extends JpaRepository<TbNudgePoint, Long> {
    List<TbNudgePoint> findByUserIdOrderByCreatedDateDesc(String userId);
}
