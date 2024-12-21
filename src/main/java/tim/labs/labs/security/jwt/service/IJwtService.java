package tim.labs.labs.security.jwt.service;

import tim.labs.labs.database.entity.enums.Role;

public interface IJwtService {
    String generateAccessToken(String username, Long userId, Role role);
    Long getUserIdFromToken(String token);
    Role getUserRoleFromToken(String token);
    String getUsernameFromToken(final String token);
}
