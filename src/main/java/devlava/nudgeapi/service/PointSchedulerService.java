package devlava.nudgeapi.service;

import devlava.nudgeapi.entity.TbLmsMember;
import devlava.nudgeapi.repository.TbLmsMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/**
 * 포인트 지급 시스템 상세 설명
 *
 * ================================================================================================
 * 1. 스케줄러 동작 방식 (PointSchedulerService)
 * ================================================================================================
 * - 매일 오후 12시 30분에 자동 실행 (@Scheduled(cron = "0 30 12 * * ?"))
 * - 전날(yesterday) 넛지 활동을 기준으로 포인트 지급
 * - 대상자: 실장(com_code: 35)과 상담매니저(com_code: 20)만 해당
 * - 각 사용자별로 calculateAndRewardDailyPoints() 메서드 호출
 *
 * ================================================================================================
 * 2. 포인트 계산 로직 (PointService.calculateAndRewardDailyPoints)
 * ================================================================================================
 *
 * Step 1: 전날 넛지 건수 조회
 * - TB_NUDGE_DATA 테이블에서 해당 사용자의 전날 넛지 건수 조회
 * - 조건: nudge_yn = 'Y' AND consulation_date LIKE '20250811%' (예: 8월 11일)
 * - 넛지 건수가 0건이면 포인트 지급하지 않고 종료
 *
 * Step 2: 이달 총 넛지 건수 조회 (등급 계산용)
 * - TB_NUDGE_DATA 테이블에서 해당 사용자의 이달 누적 넛지 건수 조회
 * - 조건: nudge_yn = 'Y' AND consulation_date LIKE '202508%' (예: 2025년 8월)
 * - 이 수치로 현재 등급을 결정
 *
 * Step 3: 등급 계산 (PointGrade enum 기준)
 * - Bronze: 50건 이하 → 보너스 0% (bonusRate = 0.0)
 * - Silver: 51~100건 → 보너스 10% (bonusRate = 0.1)
 * - Gold: 101~150건 → 보너스 20% (bonusRate = 0.2)
 * - Platinum: 151건 이상 → 보너스 30% (bonusRate = 0.3)
 *
 * Step 4: 포인트 계산
 * - 기본 포인트 = 전날 넛지 건수 × 50포인트
 * - 보너스 포인트 = 기본 포인트 × 등급별 보너스 비율
 * - 총 지급 포인트 = 기본 포인트 + 보너스 포인트
 *
 * 예시 계산:
 * 만약 어떤 사용자가 이달에 총 120건의 넛지를 했고, 어제 5건을 했다면
 * - 현재 등급: Gold (120건 → 101~150건 범위)
 * - 기본 포인트: 5건 × 50 = 250포인트
 * - 보너스 포인트: 250 × 0.2 = 50포인트
 * - 총 지급 포인트: 250 + 50 = 300포인트
 *
 * Step 5: 포인트 기록 저장
 * - TB_NUDGE_POINT 테이블에 포인트 적립 내역 저장
 * - 저장 정보: 사용자ID, 포인트수량, 적립사유, 넛지건수, 등급, 보너스비율 등
 *
 * Step 6: 사용자 포인트 요약 업데이트
 * - TB_USER_POINT_SUMMARY 테이블에서 해당 사용자 정보 업데이트
 * - 총 보유 포인트 += 금일 지급 포인트
 * - 현재 등급 = 이달 넛지 건수 기준 등급
 * - 이달 넛지 건수 = Step 2에서 조회한 값
 *
 * ================================================================================================
 * 3. 데이터 흐름 예시
 * ================================================================================================
 *
 * 사용자: csm1_chief01 (CS 마케팅 1실 실장A)
 *
 * [8월 12일 12:30 스케줄러 실행]
 * 1. 전날(8월 11일) 넛지 건수 조회 → 3건 발견
 * 2. 8월 누적 넛지 건수 조회 → 총 25건 (Bronze 등급)
 * 3. 포인트 계산:
 *    - 기본: 3건 × 50 = 150포인트
 *    - 보너스: 150 × 0% = 0포인트 (Bronze 등급)
 *    - 총합: 150포인트
 * 4. TB_NUDGE_POINT에 기록 저장
 * 5. TB_USER_POINT_SUMMARY 업데이트:
 *    - 기존 총 포인트: 500 → 650 (150 추가)
 *    - 현재 등급: bronze 유지
 *    - 이달 넛지 건수: 25건으로 업데이트
 *
 * ================================================================================================
 * 4. 등급별 혜택 상세
 * ================================================================================================
 *
 * Bronze (0~50건): 기본 포인트만 지급
 * - 예: 넛지 2건 → 2 × 50 = 100포인트
 *
 * Silver (51~100건): 기본 포인트 + 10% 보너스
 * - 예: 넛지 2건 → (2 × 50) + (100 × 0.1) = 100 + 10 = 110포인트
 *
 * Gold (101~150건): 기본 포인트 + 20% 보너스
 * - 예: 넛지 2건 → (2 × 50) + (100 × 0.2) = 100 + 20 = 120포인트
 *
 * Platinum (151건+): 기본 포인트 + 30% 보너스
 * - 예: 넛지 2건 → (2 × 50) + (100 × 0.3) = 100 + 30 = 130포인트
 *
 * ================================================================================================
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class PointSchedulerService {

    private final PointService pointService;
    private final TbLmsMemberRepository memberRepository; // 전체 직원 조회용

    /**
     * 일일 포인트 지급 스케줄러
     * - 실행시간: 매일 오후 12시 30분
     * - 대상자: 실장(com_code: 35)과 상담매니저(com_code: 20)
     * - 지급기준: 전날 넛지 활동 건수 × 50포인트 + 등급별 보너스
     */
    @Scheduled(cron = "0 44 00 * * ?")
    public void dailyPointReward() {
        log.info("일일 포인트 지급 스케줄러 시작");

        // 어제 날짜 계산 (yyyyMMdd 형식으로 변환)
        // 예: 2025-08-12 실행 시 → "20250811" 문자열 생성
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String dateStr = yesterday.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // 포인트 지급 대상자 조회: 실장과 상담매니저만
        // com_code가 35(실장) 또는 20(상담매니저)인 사용자 목록 조회
        List<TbLmsMember> targetMembers = memberRepository.findByComCodeIn(Arrays.asList("35", "20"));

        int successCount = 0; // 성공 건수
        int errorCount = 0;   // 실패 건수

        // 각 대상자별로 포인트 지급 처리
        for (TbLmsMember member : targetMembers) {
            try {
                // 개별 사용자의 포인트 계산 및 지급 처리
                pointService.calculateAndRewardDailyPoints(member.getUserId(), dateStr);
                successCount++;
                log.debug("포인트 지급 완료: {} ({})", member.getUserId(), member.getMbName());
            } catch (Exception e) {
                errorCount++;
                log.error("포인트 지급 실패: {} ({}), 오류: {}",
                        member.getUserId(), member.getMbName(), e.getMessage());
            }
        }

        log.info("일일 포인트 지급 완료 - 성공: {}건, 실패: {}건", successCount, errorCount);
    }
}
