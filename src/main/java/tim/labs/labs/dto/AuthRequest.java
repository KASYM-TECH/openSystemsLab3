package tim.labs.labs.dto;

import tim.labs.labs.database.entity.User;
import tim.labs.labs.exception.InvalidUserCredentialsException;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;

@Data
public class AuthRequest {
    private String username;
    private String password;

    public User asUser(PasswordEncoder passwordEncoder) throws InvalidUserCredentialsException {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        return user;
    }

}

