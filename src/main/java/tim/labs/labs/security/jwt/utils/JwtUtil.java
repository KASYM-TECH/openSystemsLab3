package tim.labs.labs.security.jwt.utils;

import tim.labs.labs.database.entity.enums.Role;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@PropertySource("classpath:jwt.cfg")
public class JwtUtil implements IJwtUtil {

    @Value("${tokenLifeTime}")
    private String tokenLifeTimeMs;

    @Value("${secret}")
    private String secret;

    @Override
    public String generateAccessToken(String username, Long userId, Role role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role.toString());
        claims.put("username", username);

        Instant start = Instant.now();
        Duration duration = Duration.ofMillis(Long.parseLong(tokenLifeTimeMs));
        Instant end = start.plus(duration);

        return Jwts.builder().setClaims(claims)
                .setSubject(username).setIssuedAt(Date.from(start))
                .setExpiration(Date.from(end)).signWith(SignatureAlgorithm.HS256, secret).compact();
    }

    @Override
    public String getUserNameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    @Override
    public Long getUserIdFromToken(String token) {
        return ((Integer) Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .get("userId")).longValue();
    }

    @Override
    public Role getUserRoleFromToken(String token) {
        String roleString = (String) Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .get("role");

        return Role.valueOf(roleString);
    }

    @Override
    public boolean validateAccessToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (JwtException ex) {
            return false;
        }
    }

    @Override
    public long getTokenIssuedAt(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .getIssuedAt()
                .getTime();
    }

    @Override
    public long getTokenExpiration(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .getTime();
    }
}
