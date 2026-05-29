package in.sigma.SpringProject_01.response;

import in.sigma.SpringProject_01.dto.ErrorDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ErrorResponse {

    private String status;
    private String code;
    private String message;
    private List<ErrorDto> errors;
}
