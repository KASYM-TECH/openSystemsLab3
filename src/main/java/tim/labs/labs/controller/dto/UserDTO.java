package tim.labs.labs.controller.dto;

import tim.labs.labs.database.entity.enums.Role;
import lombok.Data;

@Data
public class UserDTO {
    private String username;
    private String password;
    private Role role;
}
