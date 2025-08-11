package devlava.nudgeapi.service;


import devlava.nudgeapi.dto.HttpResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResponseService {

    public <T> HttpResponseDto getSuccessHttpResponseDto(T data) {
        HttpResponseDto responseDTO = new HttpResponseDto();

        responseDTO.setResult(true);
        responseDTO.setErrorMessage(null);
        responseDTO.setData(data);

        return responseDTO;
    }

    public <T> HttpResponseDto getFailHttpResponseDto(String errorMessage) {
        HttpResponseDto responseDTO = new HttpResponseDto();

        responseDTO.setResult(false);
        responseDTO.setErrorMessage(errorMessage);
        responseDTO.setData(null);

        return responseDTO;
    }

    public <T> HttpResponseDto getFailHttpResponseDto(String errorMessage, T data) {
        HttpResponseDto responseDTO = new HttpResponseDto();

        responseDTO.setResult(false);
        responseDTO.setErrorMessage(errorMessage);
        responseDTO.setData(data);

        return responseDTO;
    }

}