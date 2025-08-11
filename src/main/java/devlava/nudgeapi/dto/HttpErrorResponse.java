package devlava.nudgeapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class HttpErrorResponse {
    private String errorCode;
    private String message;
    private LocalDateTime timestamp;
}
