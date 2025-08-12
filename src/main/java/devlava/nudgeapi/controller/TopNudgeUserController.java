package devlava.nudgeapi.controller;

import devlava.nudgeapi.dto.HttpResponseDto;
import devlava.nudgeapi.dto.TopNudgeUserDto;
import devlava.nudgeapi.service.ResponseService;
import devlava.nudgeapi.service.TopNudgeUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/top-nudge-users")
@RequiredArgsConstructor
public class TopNudgeUserController {

    private final TopNudgeUserService topNudgeUserService;
    private final ResponseService responseService;

    /**
     * 이번달 기준으로 nudgeYn이 가장 많은 사용자 1위, 2위 조회 (최근 넛지 멘트 포함)
     */
    @GetMapping
    public HttpResponseDto getTopNudgeUsers() {
        try {
            List<TopNudgeUserDto> topUsers = topNudgeUserService.getTopNudgeUsers();
            return responseService.getSuccessHttpResponseDto(topUsers);
        } catch (Exception e) {
            return responseService.getFailHttpResponseDto("이번달 넛지 사용자 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
