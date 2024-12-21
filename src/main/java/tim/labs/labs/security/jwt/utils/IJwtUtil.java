package tim.labs.labs.security.jwt.utils;

import tim.labs.labs.database.entity.enums.Role;

public interface IJwtUtil {

    String generateAccessToken(String username, Long userId, Role role);

    String getUserNameFromToken(String token);
    Long getUserIdFromToken(String token);

    boolean validateAccessToken(String token);
    long getTokenIssuedAt(String token);
    Role getUserRoleFromToken(String token);
    long getTokenExpiration(String token);
}
