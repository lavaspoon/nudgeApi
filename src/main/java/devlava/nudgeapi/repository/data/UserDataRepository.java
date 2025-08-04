package devlava.nudgeapi.repository.data;

import devlava.nudgeapi.entity.data.UserData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDataRepository extends JpaRepository<UserData, Long> {

    List<UserData> findByUserId(Long userId);

    List<UserData> findByUserIdAndDataType(Long userId, String dataType);
}