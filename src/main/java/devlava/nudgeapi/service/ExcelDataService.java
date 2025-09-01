package devlava.nudgeapi.service;

import devlava.nudgeapi.config.DeptConfig;
import devlava.nudgeapi.dto.ExcelDataDto;
import devlava.nudgeapi.entity.TbLmsMember;
import devlava.nudgeapi.entity.TbNudgeData;
import devlava.nudgeapi.repository.TbLmsMemberRepository;
import devlava.nudgeapi.repository.TbNudgeDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExcelDataService {

    private final TbNudgeDataRepository tbNudgeDataRepository;
    private final TbLmsMemberRepository tbLmsMemberRepository;
    private final DeptConfig deptConfig;

    /**
     * 선택한 월 기준으로 통계 데이터를 추출합니다.
     * 
     * @param targetMonth "YYYY-MM" 형식의 월
     * @return 엑셀 통계 데이터
     */
    public ExcelDataDto.ExcelStatisticsData getExcelStatisticsData(String targetMonth) {
        // 해당 월의 시작일과 종료일 계산
        LocalDate startDate = LocalDate.parse(targetMonth + "-01");
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        String startDateStr = startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String endDateStr = endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // 대상 부서 목록
        List<Integer> targetDepts = deptConfig.getAdminDashboardTargetDepts();

        // 1. 부서별 통합 통계
        List<ExcelDataDto.DeptStatistics> deptStatistics = getDeptStatistics(targetDepts, startDateStr, endDateStr);

        // 2. 구성원별 통합 통계
        List<ExcelDataDto.MemberStatistics> memberStatistics = getMemberStatistics(targetDepts, startDateStr,
                endDateStr);

        return ExcelDataDto.ExcelStatisticsData.builder()
                .targetMonth(targetMonth)
                .deptStatistics(deptStatistics)
                .memberStatistics(memberStatistics)
                .build();
    }

    /**
     * 부서별 통합 통계를 계산합니다.
     */
    private List<ExcelDataDto.DeptStatistics> getDeptStatistics(List<Integer> targetDepts, String startDate,
            String endDate) {
        List<ExcelDataDto.DeptStatistics> result = new ArrayList<>();

        for (Integer deptIdx : targetDepts) {
            // 부서 정보 조회
            List<TbLmsMember> members = tbLmsMemberRepository.findByDeptIdx(deptIdx);
            if (members.isEmpty())
                continue;

            String deptName = members.get(0).getDeptName();
            List<String> userIds = members.stream()
                    .map(TbLmsMember::getUserId)
                    .collect(Collectors.toList());

            // 전체 건수 조회
            Long totalCount = tbNudgeDataRepository.countByUserIdInAndConsultationDateBetween(userIds, startDate,
                    endDate);

            // 넛지 건수 조회
            Long nudgeCount = tbNudgeDataRepository.countByUserIdInAndConsultationDateBetweenAndNudgeYn(userIds,
                    startDate, endDate, "Y");

            // 긍정 건수 조회
            Long positiveCount = tbNudgeDataRepository
                    .countByUserIdInAndConsultationDateBetweenAndNudgeYnAndCustomerConsentYn(userIds, startDate,
                            endDate, "Y", "Y");

            // 마케팅 유형별 건수 조회
            Long gigaCount = tbNudgeDataRepository.countByUserIdInAndConsultationDateBetweenAndNudgeYnAndMarketingType(
                    userIds, startDate, endDate, "Y", "GIGA%");
            Long crmCount = tbNudgeDataRepository.countByUserIdInAndConsultationDateBetweenAndNudgeYnAndMarketingType(
                    userIds, startDate, endDate, "Y", "CRM%");
            Long tdsCount = tbNudgeDataRepository.countByUserIdInAndConsultationDateBetweenAndNudgeYnAndMarketingType(
                    userIds, startDate, endDate, "Y", "TDS%");

            // 비율 계산
            Double nudgeRate = totalCount > 0 ? (double) nudgeCount / totalCount * 100 : 0.0;
            Double positiveRate = nudgeCount > 0 ? (double) positiveCount / nudgeCount * 100 : 0.0;
            Double gigaRate = nudgeCount > 0 ? (double) gigaCount / nudgeCount * 100 : 0.0;
            Double crmRate = nudgeCount > 0 ? (double) crmCount / nudgeCount * 100 : 0.0;
            Double tdsRate = nudgeCount > 0 ? (double) tdsCount / nudgeCount * 100 : 0.0;

            result.add(ExcelDataDto.DeptStatistics.builder()
                    .deptIdx(deptIdx)
                    .deptName(deptName)
                    .totalCount(totalCount)
                    .nudgeCount(nudgeCount)
                    .positiveCount(positiveCount)
                    .gigaCount(gigaCount)
                    .crmCount(crmCount)
                    .tdsCount(tdsCount)
                    .nudgeRate(Math.round(nudgeRate * 100.0) / 100.0)
                    .positiveRate(Math.round(positiveRate * 100.0) / 100.0)
                    .gigaRate(Math.round(gigaRate * 100.0) / 100.0)
                    .crmRate(Math.round(crmRate * 100.0) / 100.0)
                    .tdsRate(Math.round(tdsRate * 100.0) / 100.0)
                    .build());
        }

        return result;
    }

    /**
     * 구성원별 통합 통계를 계산합니다.
     */
    private List<ExcelDataDto.MemberStatistics> getMemberStatistics(List<Integer> targetDepts, String startDate,
            String endDate) {
        List<ExcelDataDto.MemberStatistics> result = new ArrayList<>();

        for (Integer deptIdx : targetDepts) {
            // 부서 구성원 조회
            List<TbLmsMember> members = tbLmsMemberRepository.findByDeptIdx(deptIdx);
            if (members.isEmpty())
                continue;

            String deptName = members.get(0).getDeptName();

            for (TbLmsMember member : members) {
                String userId = member.getUserId();

                // 전체 건수 조회
                Long totalCount = tbNudgeDataRepository.countByUserIdAndConsultationDateBetween(userId, startDate,
                        endDate);

                // 넛지 건수 조회
                Long nudgeCount = tbNudgeDataRepository.countByUserIdAndConsultationDateBetweenAndNudgeYn(userId,
                        startDate, endDate, "Y");

                // 긍정 건수 조회
                Long positiveCount = tbNudgeDataRepository
                        .countByUserIdAndConsultationDateBetweenAndNudgeYnAndCustomerConsentYn(userId, startDate,
                                endDate, "Y", "Y");

                // 마케팅 유형별 건수 조회
                Long gigaCount = tbNudgeDataRepository
                        .countByUserIdAndConsultationDateBetweenAndNudgeYnAndMarketingType(userId, startDate, endDate,
                                "Y", "GIGA%");
                Long crmCount = tbNudgeDataRepository.countByUserIdAndConsultationDateBetweenAndNudgeYnAndMarketingType(
                        userId, startDate, endDate, "Y", "CRM%");
                Long tdsCount = tbNudgeDataRepository.countByUserIdAndConsultationDateBetweenAndNudgeYnAndMarketingType(
                        userId, startDate, endDate, "Y", "TDS%");

                // 비율 계산
                Double nudgeRate = totalCount > 0 ? (double) nudgeCount / totalCount * 100 : 0.0;
                Double positiveRate = nudgeCount > 0 ? (double) positiveCount / nudgeCount * 100 : 0.0;
                Double gigaRate = nudgeCount > 0 ? (double) gigaCount / nudgeCount * 100 : 0.0;
                Double crmRate = nudgeCount > 0 ? (double) crmCount / nudgeCount * 100 : 0.0;
                Double tdsRate = nudgeCount > 0 ? (double) tdsCount / nudgeCount * 100 : 0.0;

                result.add(ExcelDataDto.MemberStatistics.builder()
                        .deptIdx(deptIdx)
                        .deptName(deptName)
                        .userId(userId)
                        .mbName(member.getMbName())
                        .totalCount(totalCount)
                        .nudgeCount(nudgeCount)
                        .positiveCount(positiveCount)
                        .gigaCount(gigaCount)
                        .crmCount(crmCount)
                        .tdsCount(tdsCount)
                        .nudgeRate(Math.round(nudgeRate * 100.0) / 100.0)
                        .positiveRate(Math.round(positiveRate * 100.0) / 100.0)
                        .gigaRate(Math.round(gigaRate * 100.0) / 100.0)
                        .crmRate(Math.round(crmRate * 100.0) / 100.0)
                        .tdsRate(Math.round(tdsRate * 100.0) / 100.0)
                        .build());
            }
        }

        return result;
    }
}
