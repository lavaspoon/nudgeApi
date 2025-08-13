package devlava.nudgeapi.repository;

import devlava.nudgeapi.entity.TbLmsDept;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TbLmsDeptRepository extends JpaRepository<TbLmsDept, Integer> {

    /**
     * 특정 부서의 모든 하위 부서 조회 (재귀)
     */
    @Query(value = """
            WITH RECURSIVE dept_tree AS (
                SELECT id, dept_name, parent_dept_id, depth, use_yn
                FROM TB_LMS_DEPT
                WHERE id = :deptId AND use_yn = 'Y'

                UNION ALL

                SELECT d.id, d.dept_name, d.parent_dept_id, d.depth, d.use_yn
                FROM TB_LMS_DEPT d
                INNER JOIN dept_tree dt ON d.parent_dept_id = dt.id
                WHERE d.use_yn = 'Y'
            )
            SELECT * FROM dept_tree
            """, nativeQuery = true)
    List<TbLmsDept> findAllChildDepts(@Param("deptId") Integer deptId);

    /**
     * 특정 부서의 직계 하위 부서만 조회
     */
    @Query("SELECT d FROM TbLmsDept d WHERE d.parent.id = :parentId AND d.useYn = :useYn")
    List<TbLmsDept> findByParentIdAndUseYn(@Param("parentId") Integer parentId, @Param("useYn") String useYn);

    /**
     * 최상위 부서들 조회 (parent가 null인 부서들)
     */
    @Query("SELECT d FROM TbLmsDept d WHERE d.parent IS NULL AND d.useYn = :useYn")
    List<TbLmsDept> findByParentIsNullAndUseYn(@Param("useYn") String useYn);

    /**
     * 타겟 부서
     */
    @Query("SELECT d FROM TbLmsDept d WHERE d.id IN :userDeptIdx AND d.useYn = 'Y'")
    List<TbLmsDept> findByTargetDepts(@Param("userDeptIdx") List<Integer> userDeptIdx);
}
