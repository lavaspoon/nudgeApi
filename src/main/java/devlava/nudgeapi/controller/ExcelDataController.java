package devlava.nudgeapi.controller;

import devlava.nudgeapi.dto.ExcelDataDto;
import devlava.nudgeapi.service.ExcelDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 엑셀 데이터 추출을 위한 컨트롤러
 */
@RestController
@RequestMapping("/api/excel")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ExcelDataController {

    private final ExcelDataService excelDataService;

    /**
     * 선택한 월 기준으로 통계 데이터를 추출합니다.
     * 
     * @param targetMonth "YYYY-MM" 형식의 월 (예: "2024-01")
     * @return 엑셀 통계 데이터
     */
    @GetMapping("/statistics/{targetMonth}")
    public ResponseEntity<ExcelDataDto.ExcelStatisticsData> getExcelStatisticsData(
            @PathVariable String targetMonth) {

        try {
            // 입력 형식 검증
            if (!targetMonth.matches("\\d{4}-\\d{2}")) {
                return ResponseEntity.badRequest().build();
            }

            ExcelDataDto.ExcelStatisticsData data = excelDataService.getExcelStatisticsData(targetMonth);
            return ResponseEntity.ok(data);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}
