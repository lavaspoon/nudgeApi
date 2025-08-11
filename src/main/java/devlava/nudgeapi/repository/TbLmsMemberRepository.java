package devlava.nudgeapi.repository;

import devlava.nudgeapi.entity.TbLmsMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TbLmsMemberRepository extends JpaRepository<TbLmsMember, String> {
    /**
     * 특정 com_code 목록에 해당하는 사용자 조회
     * @param comCodes com_code 목록 (예: ["35", "20"])
     * @return 해당하는 사용자 목록
     */
    List<TbLmsMember> findByComCodeIn(List<String> comCodes);
}