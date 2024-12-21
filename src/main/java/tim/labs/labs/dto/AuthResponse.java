package tim.labs.labs.dto;

import tim.labs.labs.database.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse extends BaseResponse<AuthResponse> {
    private String token;
    private User logged_as;
}
