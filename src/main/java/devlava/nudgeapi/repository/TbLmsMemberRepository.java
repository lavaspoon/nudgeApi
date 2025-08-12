package devlava.nudgeapi.repository;

import devlava.nudgeapi.entity.TbLmsMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TbLmsMemberRepository extends JpaRepository<TbLmsMember, String> {
    /**
     * 특정 com_code 목록에 해당하는 사용자 조회
     * 
     * @param comCodes com_code 목록 (예: ["35", "20"])
     * @return 해당하는 사용자 목록
     */
    List<TbLmsMember> findByComCodeIn(List<String> comCodes);

    /**
     * 특정 부서의 사용자들 조회
     */
    List<TbLmsMember> findByDeptIdx(Integer deptIdx);

    /**
     * 특정 부서 목록의 사용자들 조회
     */
    List<TbLmsMember> findByDeptIdxIn(List<Integer> deptIdxList);

    /**
     * comCode가 35 이상인 사용자 조회
     */
    @Query("SELECT m FROM TbLmsMember m WHERE CAST(m.comCode AS integer) >= 35")
    List<TbLmsMember> findUsersWithComCode35OrHigher();

    /**
     * comCode가 45 이상인 사용자 조회
     */
    @Query("SELECT m FROM TbLmsMember m WHERE CAST(m.comCode AS integer) >= 45")
    List<TbLmsMember> findUsersWithComCode45OrHigher();
}