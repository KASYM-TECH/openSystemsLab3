package tim.labs.labs.controller.dto;

import tim.labs.labs.database.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginDTO {
    private long userId;
    private String username;
    private String token;
    private Role role;
}
