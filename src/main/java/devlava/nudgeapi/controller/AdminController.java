package devlava.nudgeapi.controller;

import devlava.nudgeapi.dto.AdminDashboardDto;
import devlava.nudgeapi.dto.HttpResponseDto;
import devlava.nudgeapi.service.AdminService;
import devlava.nudgeapi.service.ResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final ResponseService responseService;

    /**
     * 관리자 대시보드 데이터 조회
     * 
     * @param userId 사용자 ID (Path Variable)
     * @return 관리자 대시보드 데이터
     */
    @GetMapping("/dashboard/{userId}")
    public ResponseEntity<HttpResponseDto> getAdminDashboard(@PathVariable String userId) {
        try {
            log.info("관리자 대시보드 조회 요청: userId={}", userId);

            AdminDashboardDto dashboardData = adminService.getAdminDashboard(userId);

            return ResponseEntity.ok(responseService.getSuccessHttpResponseDto(dashboardData));

        } catch (RuntimeException e) {
            log.error("관리자 대시보드 조회 실패: userId={}, error={}", userId, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(responseService.getFailHttpResponseDto(e.getMessage()));
        } catch (Exception e) {
            log.error("관리자 대시보드 조회 중 오류 발생: userId={}", userId, e);
            return ResponseEntity.internalServerError()
                    .body(responseService.getFailHttpResponseDto("서버 오류가 발생했습니다."));
        }
    }
}
