package devlava.nudgeapi;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashService {

    // 실제 구현에서는 Repository나 외부 API를 통해 데이터를 조회
    // private final NudgeRepository nudgeRepository;

    public DashDto getDashboardData() {
        // 이달 통계 데이터 조회
        DashDto.MonthAnalyze monthAnalyze = getMonthAnalyze();

        // 전일 통계 데이터 조회
        DashDto.CurrentAnalyze currentAnalyze = getCurrentAnalyze();

        // 이달 전체 데이터 조회
        List<DashDto.NudgeResponseDto> monthDatas = getMonthNudgeData();

        // 전일 전체 데이터 조회
        List<DashDto.NudgeResponseDto> currentDatas = getCurrentNudgeData();

        return DashDto.builder()
                .monthAnalyze(monthAnalyze)
                .currentAnalyze(currentAnalyze)
                .monthDatas(monthDatas)
                .curnetDatas(currentDatas)
                .build();
    }

    private DashDto.MonthAnalyze getMonthAnalyze() {
        // 실제 구현에서는 DB에서 이달 데이터를 조회
        // YearMonth currentMonth = YearMonth.now();
        // List<Nudge> monthlyData = nudgeRepository.findByMonth(currentMonth);

        return DashDto.MonthAnalyze.builder()
                .totalCount(500)
                .nudgeCount(20)
                .nudgePercentage(4.0)
                .gourp1Count(8)
                .gourp2Count(7)
                .gourp3Count(5)
                .build();
    }

    private DashDto.CurrentAnalyze getCurrentAnalyze() {
        // 실제 구현에서는 DB에서 전일 데이터를 조회
        // LocalDate yesterday = LocalDate.now().minusDays(1);
        // List<Nudge> yesterdayData = nudgeRepository.findByDate(yesterday);

        return DashDto.CurrentAnalyze.builder()
                .totalCount(25)
                .nudgeCount(3)
                .nudgePercentage(12.0)
                .gourp1Count(1)
                .gourp2Count(1)
                .gourp3Count(1)
                .group1Growth("+1")
                .group2Growth("0")
                .group3Growth("+1")
                .build();
    }

    private List<DashDto.NudgeResponseDto> getMonthNudgeData() {
        // 실제 구현에서는 DB에서 이달 전체 데이터를 조회
        List<DashDto.NudgeResponseDto> monthData = new ArrayList<>();

        // 7월 샘플 데이터 20개
        String[] inquiries = {"상품 문의", "계좌 개설", "대출 상담", "적금 문의", "펀드 상담",
                "보험 문의", "카드 발급", "외환 업무", "인터넷뱅킹", "모바일뱅킹"};
        String[] marketingTypes = {"GIGA 전환", "CRM 전환", "TDS 전환"};
        String[] marketingMessages = {"GIGA 상품을 추천드립니다", "CRM 시스템 전환을 제안드려요", "TDS 솔루션 도입을 권해드립니다",
                "GIGA 플랜 업그레이드 혜택이 있어요", "CRM 업무 효율화 상품이 있습니다", "TDS 전환으로 더 나은 서비스를"};

        for (int i = 1; i <= 20; i++) {
            boolean hasNudge = i <= 20; // 처음 20개는 넛지 있음
            monthData.add(DashDto.NudgeResponseDto.builder()
                    .id((long) i)
                    .consultationDate(String.format("2025-07-%02d", (i % 29) + 1))
                    .skid("EMP001") // 모든 데이터가 동일한 사번
                    .customerInquiry(inquiries[i % inquiries.length])
                    .nudgeYn(hasNudge ? "Y" : "N")
                    .marketingType(hasNudge ? marketingTypes[i % marketingTypes.length] : "")
                    .marketingMessage(hasNudge ? marketingMessages[i % marketingMessages.length] : "")
                    .customerConsentYn(hasNudge ? (i % 4 == 0 ? "N" : "Y") : "N")
                    .inappropriateResponseYn(i % 10 == 0 ? "Y" : "N")
                    .inappropriateResponseMessage(i % 10 == 0 ? "부적절한 응대가 있었습니다" : "")
                    .build());
        }

        return monthData;
    }

    private List<DashDto.NudgeResponseDto> getCurrentNudgeData() {
        // 실제 구현에서는 DB에서 전일 전체 데이터를 조회
        List<DashDto.NudgeResponseDto> currentData = new ArrayList<>();

        String yesterday = LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        String[] inquiries = {"계좌 문의", "대출 상담", "카드 문의", "상품 문의", "적금 상담",
                "보험 문의", "펀드 상담", "외환 업무", "인터넷뱅킹", "모바일뱅킹",
                "계좌 이체", "잔액 조회", "신용조회", "통장 재발급", "비밀번호 변경",
                "한도 조회", "이자율 문의", "수수료 문의", "약정 해지", "증명서 발급",
                "ATM 이용", "온라인 결제", "자동이체", "정기예금", "투자상품"};
        String[] marketingTypes = {"GIGA 전환", "CRM 전환", "TDS 전환"};
        String[] marketingMessages = {"GIGA 상품을 추천드립니다", "CRM 시스템 전환을 제안드려요", "TDS 솔루션 도입을 권해드립니다"};

        // 전일 데이터 25개 (넛지 있는 것 3개, 없는 것 22개)
        for (int i = 1; i <= 25; i++) {
            boolean hasNudge = i <= 3; // 처음 3개만 넛지 있음
            currentData.add(DashDto.NudgeResponseDto.builder()
                    .id((long) (100 + i))
                    .consultationDate(yesterday)
                    .skid("EMP001") // 모든 데이터가 동일한 사번
                    .customerInquiry(inquiries[i % inquiries.length])
                    .nudgeYn(hasNudge ? "Y" : "N")
                    .marketingType(hasNudge ? marketingTypes[(i - 1) % marketingTypes.length] : "")
                    .marketingMessage(hasNudge ? marketingMessages[(i - 1) % marketingMessages.length] : "")
                    .customerConsentYn(hasNudge ? "Y" : "N")
                    .inappropriateResponseYn("N")
                    .inappropriateResponseMessage("")
                    .build());
        }

        return currentData;
    }
}