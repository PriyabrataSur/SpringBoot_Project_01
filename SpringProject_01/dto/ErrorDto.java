package in.sigma.SpringProject_01.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorDto {

    private String field;
    private String code;
    private String message;
}
