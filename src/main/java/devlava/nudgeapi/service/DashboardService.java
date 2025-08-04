package devlava.nudgeapi.service;

import devlava.nudgeapi.dto.DashboardDto;
import devlava.nudgeapi.entity.data.NudgeConsultation;
import devlava.nudgeapi.entity.data.PointHistory;
import devlava.nudgeapi.entity.data.SuccessStory;
import devlava.nudgeapi.repository.data.NudgeConsultationRepository;
import devlava.nudgeapi.repository.data.PointHistoryRepository;
import devlava.nudgeapi.repository.data.SuccessStoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(transactionManager = "dataTransactionManager")
public class DashboardService {

    private final NudgeConsultationRepository nudgeConsultationRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final SuccessStoryRepository successStoryRepository;

    /**
     * 대시보드 데이터 조회
     */
    public DashboardDto getDashboardData(String skid) {
        String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        String yesterday = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        DashboardDto dashboardDto = new DashboardDto();

        // 월별 분석 데이터
        dashboardDto.setMonthAnalyze(getMonthAnalyze(skid, currentMonth));

        // 현재(어제) 분석 데이터
        dashboardDto.setCurrentAnalyze(getCurrentAnalyze(skid, yesterday));

        // 월별 상담 데이터
        dashboardDto.setMonthDatas(getMonthDatas(skid, currentMonth));

        // 어제 상담 데이터
        dashboardDto.setCurrentDatas(getCurrentDatas(skid, yesterday));

        // 동료 성공 사례
        dashboardDto.setColleagueSuccessStories(getColleagueSuccessStories());

        // 포인트 정보
        dashboardDto.setPointInfo(getPointInfo(skid));

        return dashboardDto;
    }

    /**
     * 월별 분석 데이터 조회
     */
    private DashboardDto.MonthAnalyzeDto getMonthAnalyze(String skid, String monthPattern) {
        Long totalCount = nudgeConsultationRepository.countBySkidAndMonth(skid, monthPattern + "%");
        Long nudgeCount = nudgeConsultationRepository.countNudgeSuccessBySkidAndMonth(skid, monthPattern + "%");

        Long group1Count = nudgeConsultationRepository.countBySkidAndMonthAndMarketingType(skid, monthPattern + "%", "GIGA 전환");
        Long group2Count = nudgeConsultationRepository.countBySkidAndMonthAndMarketingType(skid, monthPattern + "%", "CRM 전환");
        Long group3Count = nudgeConsultationRepository.countBySkidAndMonthAndMarketingType(skid, monthPattern + "%", "TDS 전환");

        double nudgePercentage = totalCount > 0 ? (double) nudgeCount / totalCount * 100 : 0.0;

        return DashboardDto.MonthAnalyzeDto.builder()
                .totalCount(totalCount.intValue())
                .nudgeCount(nudgeCount)
                .nudgePercentage(nudgePercentage)
                .group1Count(group1Count)
                .group2Count(group2Count)
                .group3Count(group3Count)
                .build();
    }

    /**
     * 현재(어제) 분석 데이터 조회
     */
    private DashboardDto.CurrentAnalyzeDto getCurrentAnalyze(String skid, String date) {
        List<NudgeConsultation> consultations = nudgeConsultationRepository.findBySkidAndConsultationDate(skid, date);

        int totalCount = consultations.size();
        long nudgeCount = consultations.stream()
                .filter(c -> "Y".equals(c.getNudgeYn()) && "Y".equals(c.getCustomerConsentYn()))
                .count();

        long group1Count = consultations.stream()
                .filter(c -> "GIGA 전환".equals(c.getMarketingType()) && "Y".equals(c.getCustomerConsentYn()))
                .count();
        long group2Count = consultations.stream()
                .filter(c -> "CRM 전환".equals(c.getMarketingType()) && "Y".equals(c.getCustomerConsentYn()))
                .count();
        long group3Count = consultations.stream()
                .filter(c -> "TDS 전환".equals(c.getMarketingType()) && "Y".equals(c.getCustomerConsentYn()))
                .count();

        double nudgePercentage = totalCount > 0 ? (double) nudgeCount / totalCount * 100 : 0.0;

        // 전일 대비 증감 계산 (실제로는 전일 데이터와 비교해야 함)
        String group1Growth = "+1";
        String group2Growth = "0";
        String group3Growth = "+1";

        return DashboardDto.CurrentAnalyzeDto.builder()
                .totalCount(totalCount)
                .nudgeCount(nudgeCount)
                .nudgePercentage(nudgePercentage)
                .group1Count(group1Count)
                .group2Count(group2Count)
                .group3Count(group3Count)
                .group1Growth(group1Growth)
                .group2Growth(group2Growth)
                .group3Growth(group3Growth)
                .build();
    }

    /**
     * 월별 상담 데이터 조회
     */
    private List<DashboardDto.NudgeResponseDto> getMonthDatas(String skid, String monthPattern) {
        List<NudgeConsultation> consultations = nudgeConsultationRepository.findBySkid(skid);

        return consultations.stream()
                .filter(c -> c.getConsultationDate().startsWith(monthPattern))
                .map(this::convertToNudgeResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 어제 상담 데이터 조회
     */
    private List<DashboardDto.NudgeResponseDto> getCurrentDatas(String skid, String date) {
        List<NudgeConsultation> consultations = nudgeConsultationRepository.findBySkidAndConsultationDate(skid, date);

        return consultations.stream()
                .map(this::convertToNudgeResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 동료 성공 사례 조회
     */
    private List<DashboardDto.SuccessStoryDto> getColleagueSuccessStories() {
        List<SuccessStory> successStories = successStoryRepository.findTop10ByCustomerConsentYnOrderByCreatedAtDesc("Y");

        return successStories.stream()
                .map(story -> convertToSuccessStoryDto(story, false)) // 북마크 기능 제거
                .collect(Collectors.toList());
    }

    /**
     * 포인트 정보 조회
     */
    private DashboardDto.PointInfoDto getPointInfo(String skid) {
        Integer currentPoints = pointHistoryRepository.findCurrentBalanceBySkid(skid);
        if (currentPoints == null) currentPoints = 0;

        String currentGrade = getGradeByPoints(currentPoints);
        String nextGrade = getNextGradeByPoints(currentPoints);
        int gradeProgress = calculateGradeProgress(currentPoints, currentGrade, nextGrade);

        // 이번주 적립 포인트 계산
        int weeklyEarned = calculateWeeklyEarnedPoints(skid);

        return DashboardDto.PointInfoDto.builder()
                .currentPoints(currentPoints)
                .currentGrade(currentGrade)
                .nextGrade(nextGrade)
                .gradeProgress(gradeProgress)
                .teamRank(3) // 실제로는 팀 순위 계산 로직 필요
                .weeklyEarned(weeklyEarned)
                .build();
    }

    /**
     * NudgeConsultation을 NudgeResponseDto로 변환
     */
    private DashboardDto.NudgeResponseDto convertToNudgeResponseDto(NudgeConsultation consultation) {
        return DashboardDto.NudgeResponseDto.builder()
                .id(consultation.getId())
                .consultationDate(consultation.getConsultationDate())
                .skid(consultation.getSkid())
                .customerInquiry(consultation.getCustomerInquiry())
                .nudgeYn(consultation.getNudgeYn())
                .marketingType(consultation.getMarketingType())
                .marketingMessage(consultation.getMarketingMessage())
                .customerConsentYn(consultation.getCustomerConsentYn())
                .inappropriateResponseYn(consultation.getInappropriateResponseYn())
                .inappropriateResponseMessage(consultation.getInappropriateResponseMessage())
                .build();
    }

    /**
     * SuccessStory를 SuccessStoryDto로 변환
     */
    private DashboardDto.SuccessStoryDto convertToSuccessStoryDto(SuccessStory story, boolean bookmarked) {
        return DashboardDto.SuccessStoryDto.builder()
                .id(story.getId())
                .consultantName(story.getConsultantName())
                .consultantLevel(story.getConsultantLevel())
                .marketingType(story.getMarketingType())
                .marketingMessage(story.getMarketingMessage())
                .customerConsentYn(story.getCustomerConsentYn())
                .bookmarked(bookmarked)
                .build();
    }

    /**
     * 포인트에 따른 등급 반환
     */
    private String getGradeByPoints(int points) {
        if (points >= 5000) return "플래티넘";
        if (points >= 2500) return "골드";
        if (points >= 1000) return "실버";
        return "브론즈";
    }

    /**
     * 다음 등급 반환
     */
    private String getNextGradeByPoints(int points) {
        if (points < 1000) return "실버";
        if (points < 2500) return "골드";
        if (points < 5000) return "플래티넘";
        return "최고 등급";
    }

    /**
     * 등급 진행률 계산
     */
    private int calculateGradeProgress(int points, String currentGrade, String nextGrade) {
        if ("최고 등급".equals(nextGrade)) return 100;

        int currentMin = getGradeMinPoints(currentGrade);
        int nextMin = getGradeMinPoints(nextGrade);

        if (nextMin == currentMin) return 100;

        return (int) ((double) (points - currentMin) / (nextMin - currentMin) * 100);
    }

    /**
     * 등급별 최소 포인트 반환
     */
    private int getGradeMinPoints(String grade) {
        switch (grade) {
            case "브론즈": return 0;
            case "실버": return 1000;
            case "골드": return 2500;
            case "플래티넘": return 5000;
            default: return 0;
        }
    }

    /**
     * 이번주 적립 포인트 계산
     */
    private int calculateWeeklyEarnedPoints(String skid) {
        // 실제로는 이번주 데이터만 필터링해야 함
        List<PointHistory> weeklyHistory = pointHistoryRepository.findBySkidAndPointTypeOrderByCreatedAtDesc(skid, "EARN");
        return weeklyHistory.stream()
                .limit(10) // 임시로 최근 10개만 계산
                .mapToInt(PointHistory::getPointAmount)
                .sum();
    }
} 