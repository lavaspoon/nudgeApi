package devlava.nudgeapi.service;

import devlava.nudgeapi.dto.AdminDashboardDto;
import devlava.nudgeapi.dto.UserDetailDto;
import devlava.nudgeapi.entity.TbLmsDept;
import devlava.nudgeapi.entity.TbLmsMember;
import devlava.nudgeapi.entity.TbNudgeData;
import devlava.nudgeapi.repository.TbLmsDeptRepository;
import devlava.nudgeapi.repository.TbLmsMemberRepository;
import devlava.nudgeapi.repository.TbNudgeDataRepository;
import devlava.nudgeapi.repository.TbNudgePointRepository;
import devlava.nudgeapi.util.WorkingDayCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

        private final TbLmsMemberRepository memberRepository;
        private final TbLmsDeptRepository deptRepository;
        private final TbNudgeDataRepository nudgeDataRepository;
        private final TbNudgePointRepository nudgePointRepository;

        /**
         * 관리자 대시보드 데이터 조회
         */
        public AdminDashboardDto getAdminDashboard(String userId) {
                // 현재 사용자 정보 조회
                TbLmsMember currentUser = memberRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + userId));

                String comCode = currentUser.getComCode();
                Integer userDeptIdx = currentUser.getDeptIdx();

                // 이번달 날짜 형식
                String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));

                List<AdminDashboardDto.DeptNudgeStats> deptStats = new ArrayList<>();
                List<AdminDashboardDto.DeptDailyStats> deptDailyStats = new ArrayList<>();

                if (Integer.parseInt(comCode) >= 45) {
                        // comCode 45 이상: 하위 부서 그룹화하여 조회
                        deptStats = getDeptStatsForHighLevelAdmin(userDeptIdx, currentMonth);
                        deptDailyStats = getDeptDailyStatsForHighLevelAdmin(userDeptIdx, currentMonth);
                } else if (Integer.parseInt(comCode) >= 35) {
                        // comCode 35 이상: 자신의 실 구성원만 조회
                        deptStats = getDeptStatsForMidLevelAdmin(userDeptIdx, currentMonth);
                        deptDailyStats = getDeptDailyStatsForMidLevelAdmin(userDeptIdx, currentMonth);
                } else {
                        throw new RuntimeException("관리자 권한이 없습니다.");
                }

                // 4가지 카테고리별 상위 5위 조회
                AdminDashboardDto.RankingStats rankings = getRankingStats(currentMonth);

                return AdminDashboardDto.builder()
                                .deptStats(deptStats)
                                .rankings(rankings)
                                .deptDailyStats(deptDailyStats)
                                .userComCode(comCode)
                                .userDeptName(currentUser.getDeptName())
                                .build();
        }

        /**
         * comCode 45 이상 관리자용 부서 통계 조회
         */
        private List<AdminDashboardDto.DeptNudgeStats> getDeptStatsForHighLevelAdmin(Integer userDeptIdx,
                        String currentMonth) {
                // parent 부서가 2 또는 3인 부서들 조회
                List<TbLmsDept> targetDepts = deptRepository.findByParentIdIn2Or3();

                // 모든 부서 ID 추출
                List<Integer> deptIds = targetDepts.stream()
                                .map(TbLmsDept::getId)
                                .collect(Collectors.toList());

                // 모든 부서의 사용자를 한 번에 조회 (N+1 방지)
                Map<Integer, List<TbLmsMember>> deptMemberMap = memberRepository.findByDeptIdxIn(deptIds)
                                .stream()
                                .collect(Collectors.groupingBy(TbLmsMember::getDeptIdx));

                // 모든 사용자 ID 추출
                List<String> allUserIds = deptMemberMap.values().stream()
                                .flatMap(members -> members.stream())
                                .map(TbLmsMember::getUserId)
                                .collect(Collectors.toList());

                // 모든 사용자의 상세 통계를 한 번에 조회 (N+1 방지)
                final Map<String, Object[]> userDetailedStatsMap = new HashMap<>();
                final Map<String, Object[]> userPrevDayStatsMap = new HashMap<>();

                if (!allUserIds.isEmpty()) {
                        // 이번달 상세 통계 조회
                        List<Object[]> detailedStats = nudgeDataRepository.findUserDetailedStatsByMonth(allUserIds,
                                        currentMonth);
                        userDetailedStatsMap.putAll(detailedStats.stream()
                                        .collect(Collectors.toMap(stat -> (String) stat[0], stat -> stat)));

                        // 전일 통계 조회
                        List<Object[]> prevDayStats = nudgeDataRepository.findUserPrevDayStats(allUserIds, "20250812");
                        userPrevDayStatsMap.putAll(prevDayStats.stream()
                                        .collect(Collectors.toMap(stat -> (String) stat[0], stat -> stat)));
                }

                // 부서별로 그룹화하여 통계 생성
                return targetDepts.stream()
                                .map(dept -> getDeptNudgeStatsWithMembersAndStats(dept.getId(), dept.getDeptName(),
                                                deptMemberMap.getOrDefault(dept.getId(), new ArrayList<>()),
                                                currentMonth,
                                                userDetailedStatsMap, userPrevDayStatsMap))
                                .filter(stats -> stats.getTotalMembers() > 0) // 구성원이 있는 부서만
                                .collect(Collectors.toList());
        }

        /**
         * comCode 35 이상 관리자용 부서 통계 조회
         */
        private List<AdminDashboardDto.DeptNudgeStats> getDeptStatsForMidLevelAdmin(Integer userDeptIdx,
                        String currentMonth) {
                // 자신의 부서만 조회
                List<TbLmsMember> members = memberRepository.findByDeptIdx(userDeptIdx);
                if (members.isEmpty()) {
                        return new ArrayList<>();
                }

                // 부서 정보 조회
                TbLmsDept dept = deptRepository.findById(userDeptIdx).orElse(null);
                String deptName = dept != null ? dept.getDeptName() : "알 수 없는 부서";

                AdminDashboardDto.DeptNudgeStats stats = getDeptNudgeStatsWithMembers(userDeptIdx, deptName, members,
                                currentMonth);
                return stats.getTotalMembers() > 0 ? List.of(stats) : new ArrayList<>();
        }

        /**
         * 특정 부서의 넛지 통계 조회 (사용자 목록 미리 조회된 버전)
         */
        private AdminDashboardDto.DeptNudgeStats getDeptNudgeStatsWithMembers(Integer deptIdx, String deptName,
                        List<TbLmsMember> members, String currentMonth) {
                if (members.isEmpty()) {
                        return AdminDashboardDto.DeptNudgeStats.builder()
                                        .deptIdx(deptIdx)
                                        .deptName(deptName)
                                        .totalMembers(0)
                                        .totalNudgeCount(0)
                                        .totalSuccessCount(0)
                                        .nudgeRate(BigDecimal.ZERO)
                                        .workingDays(0)
                                        .avgNudgePerDay(0)
                                        .userStats(new ArrayList<>())
                                        .build();
                }

                // 구성원들의 넛지 통계 조회 (N+1 방지)
                List<String> memberIds = members.stream()
                                .map(TbLmsMember::getUserId)
                                .collect(Collectors.toList());

                List<Object[]> nudgeStats = nudgeDataRepository.findNudgeStatsByUserIds(memberIds, currentMonth);

                // 통계 계산
                int totalNudgeCount = 0;
                int totalSuccessCount = 0;

                for (Object[] stat : nudgeStats) {
                        totalNudgeCount += ((Number) stat[1]).intValue();
                        totalSuccessCount += ((Number) stat[2]).intValue();
                }

                // 넛지율 계산
                BigDecimal nudgeRate = totalNudgeCount > 0
                                ? BigDecimal.valueOf(totalSuccessCount)
                                                .divide(BigDecimal.valueOf(totalNudgeCount), 4, RoundingMode.HALF_UP)
                                                .multiply(BigDecimal.valueOf(100))
                                : BigDecimal.ZERO;

                // 이번달 영업일 계산
                int workingDays = WorkingDayCalculator.calculateCurrentMonthWorkingDays();
                int avgNudgePerDay = workingDays > 0 ? totalNudgeCount / workingDays : 0;

                // 부서별 사용자 상세 통계 조회
                List<AdminDashboardDto.UserNudgeStats> userStats = getUserDetailedStats(memberIds, currentMonth);

                return AdminDashboardDto.DeptNudgeStats.builder()
                                .deptIdx(deptIdx)
                                .deptName(deptName)
                                .totalMembers(members.size())
                                .totalNudgeCount(totalNudgeCount)
                                .totalSuccessCount(totalSuccessCount)
                                .nudgeRate(nudgeRate)
                                .workingDays(workingDays)
                                .avgNudgePerDay(avgNudgePerDay)
                                .userStats(userStats)
                                .build();
        }

        /**
         * 특정 부서의 넛지 통계 조회 (사용자 목록과 통계 미리 조회된 버전)
         */
        private AdminDashboardDto.DeptNudgeStats getDeptNudgeStatsWithMembersAndStats(Integer deptIdx, String deptName,
                        List<TbLmsMember> members, String currentMonth, Map<String, Object[]> userDetailedStatsMap,
                        Map<String, Object[]> userPrevDayStatsMap) {
                if (members.isEmpty()) {
                        return AdminDashboardDto.DeptNudgeStats.builder()
                                        .deptIdx(deptIdx)
                                        .deptName(deptName)
                                        .totalMembers(0)
                                        .totalNudgeCount(0)
                                        .totalSuccessCount(0)
                                        .nudgeRate(BigDecimal.ZERO)
                                        .workingDays(0)
                                        .avgNudgePerDay(0)
                                        .userStats(new ArrayList<>())
                                        .build();
                }

                // 구성원들의 넛지 통계 조회 (N+1 방지)
                List<String> memberIds = members.stream()
                                .map(TbLmsMember::getUserId)
                                .collect(Collectors.toList());

                List<Object[]> nudgeStats = nudgeDataRepository.findNudgeStatsByUserIds(memberIds, currentMonth);

                // 통계 계산
                int totalNudgeCount = 0;
                int totalSuccessCount = 0;

                for (Object[] stat : nudgeStats) {
                        totalNudgeCount += ((Number) stat[1]).intValue();
                        totalSuccessCount += ((Number) stat[2]).intValue();
                }

                // 넛지율 계산
                BigDecimal nudgeRate = totalNudgeCount > 0
                                ? BigDecimal.valueOf(totalSuccessCount)
                                                .divide(BigDecimal.valueOf(totalNudgeCount), 4, RoundingMode.HALF_UP)
                                                .multiply(BigDecimal.valueOf(100))
                                : BigDecimal.ZERO;

                // 이번달 영업일 계산
                int workingDays = WorkingDayCalculator.calculateCurrentMonthWorkingDays();
                int avgNudgePerDay = workingDays > 0 ? totalNudgeCount / workingDays : 0;

                // 부서별 사용자 상세 통계 조회 (미리 조회된 통계 사용)
                List<AdminDashboardDto.UserNudgeStats> userStats = getUserDetailedStatsWithPreloadedData(members,
                                userDetailedStatsMap, userPrevDayStatsMap);

                return AdminDashboardDto.DeptNudgeStats.builder()
                                .deptIdx(deptIdx)
                                .deptName(deptName)
                                .totalMembers(members.size())
                                .totalNudgeCount(totalNudgeCount)
                                .totalSuccessCount(totalSuccessCount)
                                .nudgeRate(nudgeRate)
                                .workingDays(workingDays)
                                .avgNudgePerDay(avgNudgePerDay)
                                .userStats(userStats)
                                .build();
        }

        /**
         * 사용자 상세 통계 조회 (미리 조회된 통계 사용)
         */
        private List<AdminDashboardDto.UserNudgeStats> getUserDetailedStatsWithPreloadedData(List<TbLmsMember> members,
                        Map<String, Object[]> userDetailedStatsMap, Map<String, Object[]> userPrevDayStatsMap) {
                List<AdminDashboardDto.UserNudgeStats> userStats = new ArrayList<>();

                for (TbLmsMember member : members) {
                        String userId = member.getUserId();

                        // 미리 조회된 상세 통계에서 데이터 가져오기
                        Object[] detailedStat = userDetailedStatsMap.get(userId);
                        Object[] prevDayStat = userPrevDayStatsMap.get(userId);

                        int nudgeCount = 0;
                        int gigaCount = 0;
                        int crmCount = 0;
                        int tdsCount = 0;
                        BigDecimal nudgeRate = BigDecimal.ZERO;

                        if (detailedStat != null) {
                                nudgeCount = ((Number) detailedStat[2]).intValue();
                                gigaCount = ((Number) detailedStat[3]).intValue();
                                crmCount = ((Number) detailedStat[4]).intValue();
                                tdsCount = ((Number) detailedStat[5]).intValue();

                                int totalCount = ((Number) detailedStat[1]).intValue();
                                nudgeRate = totalCount > 0
                                                ? BigDecimal.valueOf(nudgeCount)
                                                                .divide(BigDecimal.valueOf(totalCount), 4,
                                                                                RoundingMode.HALF_UP)
                                                                .multiply(BigDecimal.valueOf(100))
                                                : BigDecimal.ZERO;
                        }

                        // 전일 통계
                        BigDecimal prevDayNudgeRate = BigDecimal.ZERO;
                        int prevDayNudgeCount = 0;

                        if (prevDayStat != null) {
                                int prevDayTotalCount = ((Number) prevDayStat[1]).intValue();
                                prevDayNudgeCount = ((Number) prevDayStat[2]).intValue();
                                prevDayNudgeRate = prevDayTotalCount > 0
                                                ? BigDecimal.valueOf(prevDayNudgeCount)
                                                                .divide(BigDecimal.valueOf(prevDayTotalCount), 4,
                                                                                RoundingMode.HALF_UP)
                                                                .multiply(BigDecimal.valueOf(100))
                                                : BigDecimal.ZERO;
                        }

                        userStats.add(AdminDashboardDto.UserNudgeStats.builder()
                                        .userId(userId)
                                        .userName(member.getMbName())
                                        .mbPositionName(member.getMbPositionName())
                                        .nudgeRate(nudgeRate)
                                        .nudgeCount(nudgeCount)
                                        .gigaCount(gigaCount)
                                        .crmCount(crmCount)
                                        .tdsCount(tdsCount)
                                        .prevDayNudgeRate(prevDayNudgeRate)
                                        .prevDayNudgeCount(prevDayNudgeCount)
                                        .build());
                }

                return userStats;
        }

        /**
         * 사용자별 상세 통계 조회
         */
        private List<AdminDashboardDto.UserNudgeStats> getUserDetailedStats(List<String> memberIds,
                        String currentMonth) {
                if (memberIds.isEmpty()) {
                        return new ArrayList<>();
                }

                // 이번달 상세 통계 조회
                List<Object[]> monthlyStats = nudgeDataRepository.findUserDetailedStatsByMonth(memberIds, currentMonth);

                // 전일 통계 조회
                String prevDay = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                List<Object[]> prevDayStats = nudgeDataRepository.findUserPrevDayStats(memberIds, prevDay);

                // 사용자 정보 조회
                Map<String, TbLmsMember> memberMap = memberRepository.findAllById(memberIds)
                                .stream()
                                .collect(Collectors.toMap(TbLmsMember::getUserId, member -> member));

                // 통계 데이터 매핑
                Map<String, Object[]> monthlyStatsMap = monthlyStats.stream()
                                .collect(Collectors.toMap(stat -> (String) stat[0], stat -> stat));

                Map<String, Object[]> prevDayStatsMap = prevDayStats.stream()
                                .collect(Collectors.toMap(stat -> (String) stat[0], stat -> stat));

                List<AdminDashboardDto.UserNudgeStats> userStats = new ArrayList<>();

                for (String userId : memberIds) {
                        TbLmsMember member = memberMap.get(userId);
                        if (member == null)
                                continue;

                        Object[] monthlyStat = monthlyStatsMap.get(userId);
                        Object[] prevDayStat = prevDayStatsMap.get(userId);

                        // 이번달 통계
                        int totalCount = monthlyStat != null ? ((Number) monthlyStat[1]).intValue() : 0;
                        int nudgeCount = monthlyStat != null ? ((Number) monthlyStat[2]).intValue() : 0;
                        int gigaCount = monthlyStat != null ? ((Number) monthlyStat[3]).intValue() : 0;
                        int crmCount = monthlyStat != null ? ((Number) monthlyStat[4]).intValue() : 0;
                        int tdsCount = monthlyStat != null ? ((Number) monthlyStat[5]).intValue() : 0;

                        // 전일 통계
                        int prevDayTotalCount = prevDayStat != null ? ((Number) prevDayStat[1]).intValue() : 0;
                        int prevDayNudgeCount = prevDayStat != null ? ((Number) prevDayStat[2]).intValue() : 0;

                        // 넛지율 계산
                        BigDecimal nudgeRate = totalCount > 0
                                        ? BigDecimal.valueOf(nudgeCount)
                                                        .divide(BigDecimal.valueOf(totalCount), 4, RoundingMode.HALF_UP)
                                                        .multiply(BigDecimal.valueOf(100))
                                        : BigDecimal.ZERO;

                        BigDecimal prevDayNudgeRate = prevDayTotalCount > 0
                                        ? BigDecimal.valueOf(prevDayNudgeCount)
                                                        .divide(BigDecimal.valueOf(prevDayTotalCount), 4,
                                                                        RoundingMode.HALF_UP)
                                                        .multiply(BigDecimal.valueOf(100))
                                        : BigDecimal.ZERO;

                        userStats.add(AdminDashboardDto.UserNudgeStats.builder()
                                        .userId(userId)
                                        .userName(member.getMbName())
                                        .mbPositionName(member.getMbPositionName())
                                        .nudgeRate(nudgeRate)
                                        .nudgeCount(nudgeCount)
                                        .gigaCount(gigaCount)
                                        .crmCount(crmCount)
                                        .tdsCount(tdsCount)
                                        .prevDayNudgeRate(prevDayNudgeRate)
                                        .prevDayNudgeCount(prevDayNudgeCount)
                                        .build());
                }

                return userStats;
        }

        /**
         * 4가지 카테고리별 상위 5위 통계 조회
         */
        private AdminDashboardDto.RankingStats getRankingStats(String currentMonth) {
                // 넛지 건수 상위 5위
                List<AdminDashboardDto.TopUserStats> nudgeRanking = getTopUsersByCategory(
                                nudgeDataRepository.findTop5NudgeCountByMonth(currentMonth), "nudge", currentMonth);

                // GIGA 건수 상위 5위
                List<AdminDashboardDto.TopUserStats> gigaRanking = getTopUsersByCategory(
                                nudgeDataRepository.findTop5GigaCountByMonth(currentMonth), "giga", currentMonth);

                // TDS 건수 상위 5위
                List<AdminDashboardDto.TopUserStats> tdsRanking = getTopUsersByCategory(
                                nudgeDataRepository.findTop5TdsCountByMonth(currentMonth), "tds", currentMonth);

                // CRM 건수 상위 5위
                List<AdminDashboardDto.TopUserStats> crmRanking = getTopUsersByCategory(
                                nudgeDataRepository.findTop5CrmCountByMonth(currentMonth), "crm", currentMonth);

                return AdminDashboardDto.RankingStats.builder()
                                .nudgeRanking(nudgeRanking)
                                .gigaRanking(gigaRanking)
                                .tdsRanking(tdsRanking)
                                .crmRanking(crmRanking)
                                .build();
        }

        /**
         * 카테고리별 상위 사용자 통계 조회
         */
        private List<AdminDashboardDto.TopUserStats> getTopUsersByCategory(List<Object[]> topUsersData, String category,
                        String currentMonth) {
                if (topUsersData.isEmpty()) {
                        return new ArrayList<>();
                }

                // 상위 사용자 ID 목록 추출
                List<String> topUserIds = topUsersData.stream()
                                .map(data -> (String) data[0])
                                .collect(Collectors.toList());

                // 사용자 정보 배치 조회 (N+1 방지)
                Map<String, TbLmsMember> userMap = memberRepository.findAllById(topUserIds)
                                .stream()
                                .collect(Collectors.toMap(TbLmsMember::getUserId, user -> user));

                // 포인트 정보 배치 조회 (N+1 방지)
                Map<String, Integer> pointsMap = new HashMap<>();
                List<Object[]> pointsData = nudgePointRepository.findTotalPointsByUserIdsThisMonth(topUserIds);
                for (Object[] pointData : pointsData) {
                        String userId = (String) pointData[0];
                        Integer points = ((Number) pointData[1]).intValue();
                        pointsMap.put(userId, points);
                }
                // 포인트 데이터가 없는 사용자는 0으로 설정
                for (String userId : topUserIds) {
                        if (!pointsMap.containsKey(userId)) {
                                pointsMap.put(userId, 0);
                        }
                }

                // 넛지 성공률 배치 조회 (N+1 방지)
                Map<String, Object[]> successStatsMap = new HashMap<>();
                if ("nudge".equals(category)) {
                        List<Object[]> successStats = nudgeDataRepository.findNudgeStatsByUserIds(topUserIds,
                                        currentMonth);
                        successStatsMap = successStats.stream()
                                        .collect(Collectors.toMap(stat -> (String) stat[0], stat -> stat));
                }

                List<AdminDashboardDto.TopUserStats> topUsers = new ArrayList<>();

                for (Object[] data : topUsersData) {
                        String topUserId = (String) data[0];
                        int count = ((Number) data[1]).intValue();

                        // 사용자 정보 조회
                        TbLmsMember topUser = userMap.get(topUserId);

                        // 이번달 총 포인트 조회
                        Integer totalPoints = pointsMap.get(topUserId);

                        // 카테고리별로 다른 통계 계산
                        int totalNudgeCount = 0;
                        int totalSuccessCount = 0;
                        BigDecimal nudgeRate = BigDecimal.ZERO;

                        if ("nudge".equals(category)) {
                                // 넛지 건수 카테고리의 경우 성공률도 계산
                                totalNudgeCount = count;
                                Object[] successStat = successStatsMap.get(topUserId);
                                if (successStat != null) {
                                        totalSuccessCount = ((Number) successStat[2]).intValue();
                                        nudgeRate = totalNudgeCount > 0
                                                        ? BigDecimal.valueOf(totalSuccessCount)
                                                                        .divide(BigDecimal.valueOf(totalNudgeCount), 4,
                                                                                        RoundingMode.HALF_UP)
                                                                        .multiply(BigDecimal.valueOf(100))
                                                        : BigDecimal.ZERO;
                                }
                        } else {
                                // GIGA, TDS, CRM 카테고리의 경우 넛지 성공률은 0으로 설정
                                totalNudgeCount = count;
                                totalSuccessCount = 0;
                                nudgeRate = BigDecimal.ZERO;
                        }

                        topUsers.add(AdminDashboardDto.TopUserStats.builder()
                                        .userId(topUserId)
                                        .userName(topUser != null ? topUser.getMbName() : "알 수 없음")
                                        .deptName(topUser != null ? topUser.getDeptName() : "")
                                        .totalNudgeCount(totalNudgeCount)
                                        .totalSuccessCount(totalSuccessCount)
                                        .nudgeRate(nudgeRate)
                                        .totalPoints(totalPoints != null ? totalPoints : 0)
                                        .build());
                }

                return topUsers;
        }

        /**
         * comCode 45 이상 관리자용 부서별 일자별 통계 조회
         */
        private List<AdminDashboardDto.DeptDailyStats> getDeptDailyStatsForHighLevelAdmin(Integer userDeptIdx,
                        String currentMonth) {
                // parent 부서가 2 또는 3인 부서들 조회
                List<TbLmsDept> targetDepts = deptRepository.findByParentIdIn2Or3();

                // 모든 부서 ID 추출
                List<Integer> deptIds = targetDepts.stream()
                                .map(TbLmsDept::getId)
                                .collect(Collectors.toList());

                // 부서별 일자별 통계 조회
                List<Object[]> dailyStats = nudgeDataRepository.findDeptDailyStatsByMonth(deptIds, currentMonth);

                // 부서별로 그룹화
                Map<Integer, List<Object[]>> deptStatsMap = dailyStats.stream()
                                .collect(Collectors.groupingBy(stat -> ((Number) stat[0]).intValue()));

                List<AdminDashboardDto.DeptDailyStats> deptDailyStatsList = new ArrayList<>();

                for (TbLmsDept dept : targetDepts) {
                        List<Object[]> deptStats = deptStatsMap.get(dept.getId());
                        List<AdminDashboardDto.DailyNudgeStats> dailyStatsList = new ArrayList<>();

                        if (deptStats != null) {
                                for (Object[] stat : deptStats) {
                                        String date = (String) stat[2];
                                        int totalCount = ((Number) stat[3]).intValue();
                                        int nudgeCount = ((Number) stat[4]).intValue();
                                        int gigaCount = ((Number) stat[5]).intValue();
                                        int crmCount = ((Number) stat[6]).intValue();
                                        int tdsCount = ((Number) stat[7]).intValue();

                                        // 넛지율 계산
                                        BigDecimal nudgeRate = totalCount > 0
                                                        ? BigDecimal.valueOf(nudgeCount)
                                                                        .divide(BigDecimal.valueOf(totalCount), 4,
                                                                                        RoundingMode.HALF_UP)
                                                                        .multiply(BigDecimal.valueOf(100))
                                                        : BigDecimal.ZERO;

                                        dailyStatsList.add(AdminDashboardDto.DailyNudgeStats.builder()
                                                        .date(date)
                                                        .totalCount(totalCount)
                                                        .nudgeCount(nudgeCount)
                                                        .gigaCount(gigaCount)
                                                        .crmCount(crmCount)
                                                        .tdsCount(tdsCount)
                                                        .nudgeRate(nudgeRate)
                                                        .build());
                                }
                        }

                        deptDailyStatsList.add(AdminDashboardDto.DeptDailyStats.builder()
                                        .deptIdx(dept.getId())
                                        .deptName(dept.getDeptName())
                                        .dailyStats(dailyStatsList)
                                        .build());
                }

                return deptDailyStatsList;
        }

        /**
         * comCode 35 이상 관리자용 부서별 일자별 통계 조회
         */
        private List<AdminDashboardDto.DeptDailyStats> getDeptDailyStatsForMidLevelAdmin(Integer userDeptIdx,
                        String currentMonth) {
                // 자신의 부서만 조회
                List<Object[]> dailyStats = nudgeDataRepository.findDeptDailyStatsByMonth(List.of(userDeptIdx),
                                currentMonth);

                // 부서 정보 조회
                TbLmsDept dept = deptRepository.findById(userDeptIdx).orElse(null);
                String deptName = dept != null ? dept.getDeptName() : "알 수 없는 부서";

                List<AdminDashboardDto.DailyNudgeStats> dailyStatsList = new ArrayList<>();

                for (Object[] stat : dailyStats) {
                        String date = (String) stat[2];
                        int totalCount = ((Number) stat[3]).intValue();
                        int nudgeCount = ((Number) stat[4]).intValue();
                        int gigaCount = ((Number) stat[5]).intValue();
                        int crmCount = ((Number) stat[6]).intValue();
                        int tdsCount = ((Number) stat[7]).intValue();

                        // 넛지율 계산
                        BigDecimal nudgeRate = totalCount > 0
                                        ? BigDecimal.valueOf(nudgeCount)
                                                        .divide(BigDecimal.valueOf(totalCount), 4, RoundingMode.HALF_UP)
                                                        .multiply(BigDecimal.valueOf(100))
                                        : BigDecimal.ZERO;

                        dailyStatsList.add(AdminDashboardDto.DailyNudgeStats.builder()
                                        .date(date)
                                        .totalCount(totalCount)
                                        .nudgeCount(nudgeCount)
                                        .gigaCount(gigaCount)
                                        .crmCount(crmCount)
                                        .tdsCount(tdsCount)
                                        .nudgeRate(nudgeRate)
                                        .build());
                }

                return List.of(AdminDashboardDto.DeptDailyStats.builder()
                                .deptIdx(userDeptIdx)
                                .deptName(deptName)
                                .dailyStats(dailyStatsList)
                                .build());
        }

        /**
         * 특정 사용자의 최근 5일 영업일 상세 정보 조회
         */
        public UserDetailDto getUserDetail(String targetUserId) {
                // 사용자 정보 조회
                TbLmsMember user = memberRepository.findById(targetUserId)
                                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + targetUserId));

                // 최근 5일 영업일 계산
                List<String> workingDays = WorkingDayCalculator.getRecent5WorkingDays();
                log.info("조회할 영업일: {}", workingDays);

                // 사용자의 최근 5일 영업일 데이터 조회
                List<TbNudgeData> userData = nudgeDataRepository.findUserDataByWorkingDays(
                                targetUserId,
                                workingDays.get(0),
                                workingDays.get(1),
                                workingDays.get(2),
                                workingDays.get(3),
                                workingDays.get(4));
                log.info("사용자 데이터 조회 결과: {}건", userData.size());

                // 날짜별 통계 조회
                List<Object[]> dailyStats = nudgeDataRepository.findUserDailyStatsByWorkingDays(
                                targetUserId,
                                workingDays.get(0),
                                workingDays.get(1),
                                workingDays.get(2),
                                workingDays.get(3),
                                workingDays.get(4));
                log.info("날짜별 통계 조회 결과: {}건", dailyStats.size());

                // 날짜별 통계를 Map으로 변환
                Map<String, Object[]> dailyStatsMap = dailyStats.stream()
                                .collect(Collectors.toMap(stat -> (String) stat[0], stat -> stat));

                // 날짜별 상세 데이터 구성
                List<UserDetailDto.DailyNudgeData> dailyDataList = new ArrayList<>();

                for (String workingDay : workingDays) {
                        Object[] stats = dailyStatsMap.get(workingDay);

                        int totalCount = stats != null ? ((Number) stats[1]).intValue() : 0;
                        int nudgeCount = stats != null ? ((Number) stats[2]).intValue() : 0;
                        int gigaCount = stats != null ? ((Number) stats[3]).intValue() : 0;
                        int crmCount = stats != null ? ((Number) stats[4]).intValue() : 0;
                        int tdsCount = stats != null ? ((Number) stats[5]).intValue() : 0;

                        // 넛지율 계산
                        BigDecimal nudgeRate = totalCount > 0
                                        ? BigDecimal.valueOf(nudgeCount)
                                                        .divide(BigDecimal.valueOf(totalCount), 4, RoundingMode.HALF_UP)
                                                        .multiply(BigDecimal.valueOf(100))
                                        : BigDecimal.ZERO;

                        // 해당 날짜의 상세 넛지 데이터 필터링
                        List<UserDetailDto.NudgeDetail> nudgeDetails = userData.stream()
                                        .filter(data -> workingDay.equals(data.getConsultationDate().substring(0, 8)))
                                        .filter(data -> "Y".equals(data.getNudgeYn()))
                                        .map(data -> UserDetailDto.NudgeDetail.builder()
                                                        .consultationDate(data.getConsultationDate())
                                                        .customerInquiry(data.getCustomerInquiry())
                                                        .marketingType(data.getMarketingType())
                                                        .marketingMessage(data.getMarketingMessage())
                                                        .customerConsentYn(data.getCustomerConsentYn())
                                                        .inappropriateResponseYn(data.getInappropriateResponseYn())
                                                        .inappropriateResponseMessage(
                                                                        data.getInappropriateResponseMessage())
                                                        .build())
                                        .collect(Collectors.toList());

                        // 데이터가 없어도 빈 객체로 생성 (화면에서 날짜별로 표시하기 위해)
                        dailyDataList.add(UserDetailDto.DailyNudgeData.builder()
                                        .date(workingDay)
                                        .totalCount(totalCount)
                                        .nudgeCount(nudgeCount)
                                        .gigaCount(gigaCount)
                                        .crmCount(crmCount)
                                        .tdsCount(tdsCount)
                                        .nudgeRate(nudgeRate)
                                        .nudgeDetails(nudgeDetails)
                                        .build());
                }

                // 전체 요약 통계 계산
                int totalDays = workingDays.size();
                int totalCount = dailyDataList.stream().mapToInt(UserDetailDto.DailyNudgeData::getTotalCount).sum();
                int totalNudgeCount = dailyDataList.stream().mapToInt(UserDetailDto.DailyNudgeData::getNudgeCount)
                                .sum();
                int totalGigaCount = dailyDataList.stream().mapToInt(UserDetailDto.DailyNudgeData::getGigaCount).sum();
                int totalCrmCount = dailyDataList.stream().mapToInt(UserDetailDto.DailyNudgeData::getCrmCount).sum();
                int totalTdsCount = dailyDataList.stream().mapToInt(UserDetailDto.DailyNudgeData::getTdsCount).sum();

                // 평균 넛지율 계산
                BigDecimal avgNudgeRate = totalDays > 0
                                ? dailyDataList.stream()
                                                .map(UserDetailDto.DailyNudgeData::getNudgeRate)
                                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                                                .divide(BigDecimal.valueOf(totalDays), 4, RoundingMode.HALF_UP)
                                : BigDecimal.ZERO;

                // 전체 넛지율 계산
                BigDecimal totalNudgeRate = totalCount > 0
                                ? BigDecimal.valueOf(totalNudgeCount)
                                                .divide(BigDecimal.valueOf(totalCount), 4, RoundingMode.HALF_UP)
                                                .multiply(BigDecimal.valueOf(100))
                                : BigDecimal.ZERO;

                UserDetailDto.UserSummary summary = UserDetailDto.UserSummary.builder()
                                .totalDays(totalDays)
                                .totalCount(totalCount)
                                .totalNudgeCount(totalNudgeCount)
                                .totalGigaCount(totalGigaCount)
                                .totalCrmCount(totalCrmCount)
                                .totalTdsCount(totalTdsCount)
                                .avgNudgeRate(avgNudgeRate)
                                .totalNudgeRate(totalNudgeRate)
                                .build();

                return UserDetailDto.builder()
                                .userId(user.getUserId())
                                .userName(user.getMbName())
                                .mbPositionName(user.getMbPositionName())
                                .deptName(user.getDeptName())
                                .dailyData(dailyDataList)
                                .summary(summary)
                                .build();
        }
}
