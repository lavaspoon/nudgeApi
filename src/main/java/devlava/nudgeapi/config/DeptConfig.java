package devlava.nudgeapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;

/**
 * 부서 관련 설정을 관리하는 클래스
 * 순위 계산, 통계 조회 등에서 사용되는 대상 부서들을 중앙에서 관리
 */
@Component
@ConfigurationProperties(prefix = "dept")
public class DeptConfig {

    private List<Integer> rankingTargetDepts = Arrays.asList(4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14);
    private List<Integer> adminDashboardTargetDepts = Arrays.asList(4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14);
    private List<String> pointTargetCompCodes = Arrays.asList("35", "20");

    // Getter and Setter methods
    public List<Integer> getRankingTargetDepts() {
        return rankingTargetDepts;
    }

    public void setRankingTargetDepts(List<Integer> rankingTargetDepts) {
        this.rankingTargetDepts = rankingTargetDepts;
    }

    public List<Integer> getAdminDashboardTargetDepts() {
        return adminDashboardTargetDepts;
    }

    public void setAdminDashboardTargetDepts(List<Integer> adminDashboardTargetDepts) {
        this.adminDashboardTargetDepts = adminDashboardTargetDepts;
    }

    public List<String> getPointTargetCompCodes() {
        return pointTargetCompCodes;
    }

    public void setPointTargetCompCodes(List<String> pointTargetCompCodes) {
        this.pointTargetCompCodes = pointTargetCompCodes;
    }

    /**
     * 특정 부서가 순위 계산 대상인지 확인
     * 
     * @param deptId 부서 ID
     * @return 순위 계산 대상 여부
     */
    public boolean isRankingTargetDept(Integer deptId) {
        return rankingTargetDepts.contains(deptId);
    }

    /**
     * 특정 부서가 관리자 대시보드 대상인지 확인
     * 
     * @param deptId 부서 ID
     * @return 관리자 대시보드 대상 여부
     */
    public boolean isAdminDashboardTargetDept(Integer deptId) {
        return adminDashboardTargetDepts.contains(deptId);
    }
}
