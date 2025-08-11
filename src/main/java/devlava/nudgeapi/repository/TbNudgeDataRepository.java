package devlava.nudgeapi.repository;

import devlava.nudgeapi.entity.TbNudgeData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TbNudgeDataRepository extends JpaRepository<TbNudgeData, Long> {

    @Query("SELECT COUNT(n) FROM TbNudgeData n WHERE n.userId = :userId AND n.nudgeYn = 'Y' AND n.consultationDate LIKE :datePrefix")
    Integer countNudgeByUserIdAndDate(@Param("userId") String userId, @Param("datePrefix") String datePrefix);

    @Query("SELECT COUNT(n) FROM TbNudgeData n WHERE n.userId = :userId AND n.nudgeYn = 'Y' AND n.consultationDate LIKE :monthPrefix")
    Integer countMonthlyNudgeByUserId(@Param("userId") String userId, @Param("monthPrefix") String monthPrefix);
}
