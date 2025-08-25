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
import devlava.nudgeapi.config.DeptConfig;
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
        private final DeptConfig deptConfig;

        /**
         * 관리자 대시보드 데이터 조회
         */
        public AdminDashboardDto getAdminDashboard(String userId) {
                // 기준 날짜
                String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));

                // 현재 사용자 정보 조회
                TbLmsMember member = memberRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + userId));
                Integer comCode = Integer.parseInt(member.getComCode());
                Integer userDeptIdx = member.getDeptIdx();
                List<Integer> deptIds = new ArrayList<>();

                // 하위 부서 세팅
                if (comCode >= 45) {
                        deptIds.addAll(deptConfig.getAdminDashboardTargetDepts());
                } else if (comCode >= 35) {
                        deptIds.add(userDeptIdx);
                } else {
                        throw new RuntimeException("관리자 권한이 없습니다.");
                }

                // 이달, 모든 부서의 통계 및 구성원 데이터
                List<AdminDashboardDto.DeptNudgeStats> deptStats = getDeptStatsForHighLevelAdmin(deptIds, currentMonth);
                // 이달, 실별 월별 넛지 건수
                List<AdminDashboardDto.DeptMonthlyStats> deptMonthlyStats = getDeptMonthlyStatsForHighLevelAdmin(
                                deptIds,
                                currentMonth);

                // 4가지 카테고리별 상위 5위 조회
                AdminDashboardDto.RankingStats rankings = getRankingStats(currentMonth);

                return AdminDashboardDto.builder()
                                .deptStats(deptStats)
                                .rankings(rankings)
                                .deptMonthlyStats(deptMonthlyStats)
                                .build();
        }

        /**
         * 부서 통계 조회
         */
        private List<AdminDashboardDto.DeptNudgeStats> getDeptStatsForHighLevelAdmin(List<Integer> deptId,
                        String currentMonth) {

                List<TbLmsDept> targetDepts = deptRepository.findByTargetDepts(deptId);

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
                if (!allUserIds.isEmpty()) {
                        // 이번달 상세 통계 조회
                        List<Object[]> detailedStats = nudgeDataRepository.findUserDetailedStatsByMonth(allUserIds,
                                        currentMonth);
                        userDetailedStatsMap.putAll(detailedStats.stream()
                                        .collect(Collectors.toMap(stat -> (String) stat[0], stat -> stat)));
                }

                // 전월 계산
                String prevMonth = getPreviousMonth(currentMonth);

                // 전월 부서별 통계 조회
                List<Object[]> prevMonthStats = nudgeDataRepository.findDeptMonthlyComparisonStats(deptIds, prevMonth);
                Map<Integer, Object[]> prevMonthStatsMap = prevMonthStats.stream()
                                .collect(Collectors.toMap(stat -> ((Number) stat[0]).intValue(), stat -> stat));

                // 부서별로 그룹화하여 통계 생성
                return targetDepts.stream()
                                .map(dept -> getDeptNudgeStatsWithMembersAndStats(dept.getId(), dept.getDeptName(),
                                                deptMemberMap.getOrDefault(dept.getId(), new ArrayList<>()),
                                                currentMonth,
                                                userDetailedStatsMap,
                                                prevMonthStatsMap.get(dept.getId())))
                                .filter(stats -> stats.getTotalMembers() > 0) // 구성원이 있는 부서만
                                .collect(Collectors.toList());
        }

        /**
         * 전월 계산
         */
        private String getPreviousMonth(String currentMonth) {
                LocalDate currentDate = LocalDate.parse(currentMonth + "01", DateTimeFormatter.ofPattern("yyyyMMdd"));
                LocalDate prevDate = currentDate.minusMonths(1);
                return prevDate.format(DateTimeFormatter.ofPattern("yyyyMM"));
        }

        /**
         * 특정 부서의 넛지 통계 조회 (사용자 목록과 통계 미리 조회된 버전)
         */
        private AdminDashboardDto.DeptNudgeStats getDeptNudgeStatsWithMembersAndStats(Integer deptIdx, String deptName,
                        List<TbLmsMember> members, String currentMonth, Map<String, Object[]> userDetailedStatsMap,
                        Object[] prevMonthStats) {
                if (members.isEmpty()) {
                        return AdminDashboardDto.DeptNudgeStats.builder()
                                        .deptIdx(deptIdx)
                                        .deptName(deptName)
                                        .totalMembers(0)
                                        .totalCount(0)
                                        .totalNudgeCount(0)
                                        .totalSuccessCount(0)
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

                // 이번달 영업일 계산
                int workingDays = WorkingDayCalculator.calculateCurrentMonthWorkingDays();
                int avgNudgePerDay = workingDays > 0 ? totalNudgeCount / workingDays : 0;

                // 부서별 사용자 상세 통계 조회 (미리 조회된 통계 사용)
                List<AdminDashboardDto.UserNudgeStats> userStats = getUserDetailedStatsWithPreloadedData(members,
                                userDetailedStatsMap, currentMonth);

                // 부서 전체의 totalCount 계산 (모든 사용자의 totalCount 합계)
                int totalCount = userStats.stream()
                                .mapToInt(AdminDashboardDto.UserNudgeStats::getTotalCount)
                                .sum();

                // 넛지 성공률 계산
                BigDecimal nudgeSuccessRate = totalNudgeCount > 0
                                ? BigDecimal.valueOf(totalSuccessCount)
                                                .divide(BigDecimal.valueOf(totalNudgeCount), 4, RoundingMode.HALF_UP)
                                                .multiply(BigDecimal.valueOf(100))
                                : BigDecimal.ZERO;

                // 전월 대비 비교 정보 계산
                AdminDashboardDto.MonthlyComparisonDto monthlyComparison = calculateMonthlyComparison(
                                prevMonthStats, totalNudgeCount, nudgeSuccessRate);

                return AdminDashboardDto.DeptNudgeStats.builder()
                                .deptIdx(deptIdx)
                                .deptName(deptName)
                                .totalMembers(members.size())
                                .totalCount(totalCount)
                                .totalNudgeCount(totalNudgeCount)
                                .totalSuccessCount(totalSuccessCount)
                                .nudgeSuccessRate(nudgeSuccessRate)
                                .monthlyComparison(monthlyComparison)
                                .userStats(userStats)
                                .build();
        }

        /**
         * 전월 대비 비교 정보 계산
         */
        private AdminDashboardDto.MonthlyComparisonDto calculateMonthlyComparison(Object[] prevMonthStats,
                        int currentNudgeCount, BigDecimal currentSuccessRate) {
                // 전월 데이터 추출
                int prevNudgeCount = 0;
                BigDecimal prevSuccessRate = BigDecimal.ZERO;

                if (prevMonthStats != null) {
                        prevNudgeCount = ((Number) prevMonthStats[2]).intValue(); // nudgeCount
                        int prevSuccessCount = ((Number) prevMonthStats[3]).intValue(); // successCount

                        // 전월 성공률 계산
                        prevSuccessRate = prevNudgeCount > 0
                                        ? BigDecimal.valueOf(prevSuccessCount)
                                                        .divide(BigDecimal.valueOf(prevNudgeCount), 4,
                                                                        RoundingMode.HALF_UP)
                                                        .multiply(BigDecimal.valueOf(100))
                                        : BigDecimal.ZERO;
                }

                // 넛지 건수 증감 계산
                int nudgeCountDiff = currentNudgeCount - prevNudgeCount;
                BigDecimal nudgeCountChangeRate = prevNudgeCount > 0
                                ? BigDecimal.valueOf(nudgeCountDiff)
                                                .divide(BigDecimal.valueOf(prevNudgeCount), 4, RoundingMode.HALF_UP)
                                                .multiply(BigDecimal.valueOf(100))
                                : BigDecimal.ZERO;

                // 성공률 증감 계산
                BigDecimal successRateDiff = currentSuccessRate.subtract(prevSuccessRate);
                BigDecimal successRateChangeRate = prevSuccessRate.compareTo(BigDecimal.ZERO) > 0
                                ? successRateDiff
                                                .divide(prevSuccessRate, 4, RoundingMode.HALF_UP)
                                                .multiply(BigDecimal.valueOf(100))
                                : BigDecimal.ZERO;

                // 트렌드 결정
                String nudgeCountTrend = nudgeCountDiff > 0 ? "UP" : nudgeCountDiff < 0 ? "DOWN" : "SAME";
                String successRateTrend = successRateDiff.compareTo(BigDecimal.ZERO) > 0 ? "UP"
                                : successRateDiff.compareTo(BigDecimal.ZERO) < 0 ? "DOWN" : "SAME";

                return AdminDashboardDto.MonthlyComparisonDto.builder()
                                .prevMonthNudgeCount(prevNudgeCount)
                                .currentMonthNudgeCount(currentNudgeCount)
                                .nudgeCountDiff(nudgeCountDiff)
                                .nudgeCountChangeRate(nudgeCountChangeRate.setScale(2, RoundingMode.HALF_UP))
                                .prevMonthSuccessRate(prevSuccessRate.setScale(2, RoundingMode.HALF_UP))
                                .currentMonthSuccessRate(currentSuccessRate.setScale(2, RoundingMode.HALF_UP))
                                .successRateDiff(successRateDiff.setScale(2, RoundingMode.HALF_UP))
                                .successRateChangeRate(successRateChangeRate.setScale(2, RoundingMode.HALF_UP))
                                .nudgeCountTrend(nudgeCountTrend)
                                .successRateTrend(successRateTrend)
                                .build();
        }

        /**
         * 사용자 상세 통계 조회 (미리 조회된 통계 사용)
         */
        private List<AdminDashboardDto.UserNudgeStats> getUserDetailedStatsWithPreloadedData(List<TbLmsMember> members,
                        Map<String, Object[]> userDetailedStatsMap, String currentMonth) {
                List<AdminDashboardDto.UserNudgeStats> userStats = new ArrayList<>();

                // 모든 사용자 ID 추출
                List<String> memberIds = members.stream()
                                .map(TbLmsMember::getUserId)
                                .collect(Collectors.toList());

                // 넛지 성공률 계산을 위한 배치 조회 (N+1 방지)
                Map<String, Object[]> userNudgeStatsMap = new HashMap<>();
                if (!memberIds.isEmpty()) {
                        List<Object[]> userNudgeStats = nudgeDataRepository.findNudgeStatsByUserIds(memberIds,
                                        currentMonth);
                        userNudgeStatsMap = userNudgeStats.stream()
                                        .collect(Collectors.toMap(stat -> (String) stat[0], stat -> stat));
                }

                for (TbLmsMember member : members) {
                        String userId = member.getUserId();

                        // 미리 조회된 상세 통계에서 데이터 가져오기
                        Object[] detailedStat = userDetailedStatsMap.get(userId);

                        int nudgeCount = 0;
                        int gigaCount = 0;
                        int crmCount = 0;
                        int tdsCount = 0;

                        int totalCount = 0;
                        if (detailedStat != null) {
                                totalCount = ((Number) detailedStat[1]).intValue();
                                nudgeCount = ((Number) detailedStat[2]).intValue();
                                gigaCount = ((Number) detailedStat[3]).intValue();
                                crmCount = ((Number) detailedStat[4]).intValue();
                                tdsCount = ((Number) detailedStat[5]).intValue();
                        }

                        // 배치 조회된 넛지 통계에서 성공 건수 가져오기
                        int userSuccessCount = 0;
                        Object[] userNudgeStat = userNudgeStatsMap.get(userId);
                        if (userNudgeStat != null) {
                                userSuccessCount = ((Number) userNudgeStat[2]).intValue();
                        }

                        userStats.add(AdminDashboardDto.UserNudgeStats.builder()
                                        .userId(userId)
                                        .userName(member.getMbName())
                                        .mbPositionName(member.getMbPositionName())
                                        .totalCount(totalCount)
                                        .nudgeCount(nudgeCount)
                                        .gigaCount(gigaCount)
                                        .crmCount(crmCount)
                                        .tdsCount(tdsCount)
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
         * 랭킹
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

                        if ("nudge".equals(category)) {
                                // 넛지 건수 카테고리의 경우 성공률도 계산
                                totalNudgeCount = count;
                        } else {
                                // GIGA, TDS, CRM 카테고리의 경우 넛지 성공률은 0으로 설정
                                totalNudgeCount = count;
                                totalSuccessCount = 0;

                        }

                        topUsers.add(AdminDashboardDto.TopUserStats.builder()
                                        .userId(topUserId)
                                        .userName(topUser != null ? topUser.getMbName() : "알 수 없음")
                                        .deptName(topUser != null ? topUser.getDeptName() : "")
                                        .totalCount(count)
                                        .totalNudgeCount(totalNudgeCount)
                                        .totalSuccessCount(totalSuccessCount)
                                        .build());
                }

                return topUsers;
        }

        /**
         * 관리자용 부서별 월별 통계 조회
         */
        private List<AdminDashboardDto.DeptMonthlyStats> getDeptMonthlyStatsForHighLevelAdmin(List<Integer> userDeptIdx,
                        String currentMonth) {
                // parent 부서가 2 또는 3인 부서들 조회
                List<TbLmsDept> targetDepts = deptRepository.findByTargetDepts(userDeptIdx);

                // 모든 부서 ID 추출
                List<Integer> deptIds = targetDepts.stream()
                                .map(TbLmsDept::getId)
                                .collect(Collectors.toList());

                // 부서별 월별 통계 조회 (최근 6개월)
                List<String> months = getRecent6Months();
                List<Object[]> monthlyStats = nudgeDataRepository.findDeptMonthlyStatsByMonths(deptIds, months);

                // 부서별로 그룹화
                Map<Integer, List<Object[]>> deptStatsMap = monthlyStats.stream()
                                .collect(Collectors.groupingBy(stat -> ((Number) stat[0]).intValue()));

                List<AdminDashboardDto.DeptMonthlyStats> deptMonthlyStatsList = new ArrayList<>();

                for (TbLmsDept dept : targetDepts) {
                        List<Object[]> deptStats = deptStatsMap.get(dept.getId());
                        List<AdminDashboardDto.MonthlyNudgeStats> monthlyStatsList = new ArrayList<>();

                        if (deptStats != null) {
                                for (Object[] stat : deptStats) {
                                        String month = (String) stat[2];
                                        int totalCount = ((Number) stat[3]).intValue();
                                        int nudgeCount = ((Number) stat[4]).intValue();
                                        int successCount = ((Number) stat[5]).intValue();
                                        int gigaCount = ((Number) stat[6]).intValue();
                                        int crmCount = ((Number) stat[7]).intValue();
                                        int tdsCount = ((Number) stat[8]).intValue();

                                        // 넛지율 계산 (nudgeCount / totalCount) - 소수 첫째자리까지
                                        BigDecimal nudgeRate = totalCount > 0
                                                        ? BigDecimal.valueOf(nudgeCount)
                                                                        .divide(BigDecimal.valueOf(totalCount), 4,
                                                                                        RoundingMode.HALF_UP)
                                                                        .multiply(BigDecimal.valueOf(100))
                                                                        .setScale(1, RoundingMode.HALF_UP)
                                                        : BigDecimal.ZERO;

                                        monthlyStatsList.add(AdminDashboardDto.MonthlyNudgeStats.builder()
                                                        .month(month)
                                                        .totalCount(totalCount)
                                                        .nudgeCount(nudgeCount)
                                                        .successCount(successCount)
                                                        .gigaCount(gigaCount)
                                                        .crmCount(crmCount)
                                                        .tdsCount(tdsCount)
                                                        .nudgeRate(nudgeRate)
                                                        .build());
                                }
                        }

                        deptMonthlyStatsList.add(AdminDashboardDto.DeptMonthlyStats.builder()
                                        .deptIdx(dept.getId())
                                        .deptName(dept.getDeptName())
                                        .monthlyStats(monthlyStatsList)
                                        .build());
                }

                return deptMonthlyStatsList;
        }

        /**
         * 최근 6개월 계산
         */
        private List<String> getRecent6Months() {
                List<String> months = new ArrayList<>();
                LocalDate currentDate = LocalDate.now();

                for (int i = 5; i >= 0; i--) {
                        LocalDate monthDate = currentDate.minusMonths(i);
                        months.add(monthDate.format(DateTimeFormatter.ofPattern("yyyyMM")));
                }

                return months;
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
