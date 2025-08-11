package devlava.nudgeapi.controller;

import devlava.nudgeapi.dto.HttpResponseDto;
import devlava.nudgeapi.dto.PointDto;
import devlava.nudgeapi.service.PointService;
import devlava.nudgeapi.service.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dash")
@RequiredArgsConstructor
public class DashController {

    private final ResponseService responseService;

    private final PointService pointService;

    @GetMapping("/point/{userId}")
    public ResponseEntity<HttpResponseDto> getPoint(@PathVariable String userId) {
        PointDto result = pointService.getPoint(userId);
        HttpResponseDto responseDto = responseService.getSuccessHttpResponseDto(result);

        return ResponseEntity.ok(responseDto);
    }
}
