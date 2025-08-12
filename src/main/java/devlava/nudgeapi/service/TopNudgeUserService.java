package devlava.nudgeapi.service;

import devlava.nudgeapi.dto.TopNudgeUserDto;
import devlava.nudgeapi.entity.TbLmsMember;
import devlava.nudgeapi.entity.TbNudgeData;
import devlava.nudgeapi.entity.TbUserPointSummary;
import devlava.nudgeapi.repository.TbLmsMemberRepository;
import devlava.nudgeapi.repository.TbNudgeDataRepository;
import devlava.nudgeapi.repository.TbUserPointSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TopNudgeUserService {

    private final TbNudgeDataRepository tbNudgeDataRepository;
    private final TbLmsMemberRepository tbLmsMemberRepository;
    private final TbUserPointSummaryRepository tbUserPointSummaryRepository;

    /**
     * 이번달 기준으로 nudgeYn이 가장 많은 사용자 1위, 2위 조회 (최근 넛지 멘트 포함)
     */
    public List<TopNudgeUserDto> getTopNudgeUsers() {
        // 이번달 날짜 형식 생성 (YYYYMM)
        String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));

        // 이번달 nudgeYn이 가장 많은 사용자 1위, 2위 조회
        List<Object[]> topUsers = tbNudgeDataRepository.findTopNudgeUsersByMonth(currentMonth);

        if (topUsers.isEmpty()) {
            return new ArrayList<>();
        }

        // userId 목록 추출
        List<String> userIds = topUsers.stream()
                .map(row -> (String) row[0])
                .collect(Collectors.toList());

        // 멤버 정보 조회
        List<TbLmsMember> members = tbLmsMemberRepository.findAllById(userIds);
        Map<String, TbLmsMember> memberMap = members.stream()
                .collect(Collectors.toMap(TbLmsMember::getUserId, member -> member));

        // 포인트 요약 정보 조회
        List<TbUserPointSummary> summaries = tbUserPointSummaryRepository.findAllById(userIds);
        Map<String, TbUserPointSummary> summaryMap = summaries.stream()
                .collect(Collectors.toMap(TbUserPointSummary::getUserId, summary -> summary));

        // 결과 DTO 생성
        List<TopNudgeUserDto> result = new ArrayList<>();
        for (int i = 0; i < topUsers.size(); i++) {
            Object[] row = topUsers.get(i);
            String userId = (String) row[0];
            Integer nudgeCount = ((Number) row[1]).intValue();

            TbLmsMember member = memberMap.get(userId);
            TbUserPointSummary summary = summaryMap.get(userId);

            // 해당 사용자의 최근 넛지 데이터 조회
            TbNudgeData latestNudge = tbNudgeDataRepository.findLatestNudgeByUserId(userId);

            if (member != null) {
                TopNudgeUserDto dto = TopNudgeUserDto.builder()
                        .userId(userId)
                        .mbName(member.getMbName())
                        .deptName(member.getDeptName())
                        .mbPositionName(member.getMbPositionName())
                        .currentGrade(summary != null ? summary.getCurrentGrade() : "bronze")
                        .monthNudgeCount(nudgeCount)
                        .rank(i + 1)
                        .latestNudgeMessage(latestNudge != null ? latestNudge.getMarketingMessage() : null)
                        .latestNudgeDate(latestNudge != null ? latestNudge.getConsultationDate() : null)
                        .build();

                result.add(dto);
            }
        }

        return result;
    }
}
