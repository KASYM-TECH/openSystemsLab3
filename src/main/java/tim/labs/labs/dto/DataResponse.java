package tim.labs.labs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;

@Data
@AllArgsConstructor
public class DataResponse<T> extends BaseResponse<DataResponse<T>> {
    private T data;

    public static <T> ResponseEntity<?> success(T data) {
        return new DataResponse<T>(data).asResponseEntity();
    }

}
