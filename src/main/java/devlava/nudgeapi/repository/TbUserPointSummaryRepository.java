package devlava.nudgeapi.repository;

import devlava.nudgeapi.entity.TbUserPointSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TbUserPointSummaryRepository extends JpaRepository<TbUserPointSummary, String> {
    Optional<TbUserPointSummary> findByUserId(String userId);
}