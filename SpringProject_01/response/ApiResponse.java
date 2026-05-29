package in.sigma.SpringProject_01.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse<T> {

    private String status;
    private String message;
    private T data;

}


