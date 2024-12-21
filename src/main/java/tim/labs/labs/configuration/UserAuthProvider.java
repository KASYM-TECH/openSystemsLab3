package tim.labs.labs.configuration;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import tim.labs.labs.database.entity.User;
import tim.labs.labs.security.jwt.utils.IJwtUtil;
import tim.labs.labs.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Component
public class UserAuthProvider {
    private final UserService userService;
    private final IJwtUtil jwtUtil;


    public Authentication validateToken(String token) {
        var userName = jwtUtil.getUserNameFromToken(token);
        User user = userService.findByUsername(userName);
        return new UsernamePasswordAuthenticationToken(user, null,
                List.of(user::getPermission));
    }

}
