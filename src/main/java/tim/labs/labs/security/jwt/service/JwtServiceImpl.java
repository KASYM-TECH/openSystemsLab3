package tim.labs.labs.security.jwt.service;

import tim.labs.labs.database.entity.enums.Role;
import tim.labs.labs.security.IValidator;
import tim.labs.labs.security.jwt.utils.IJwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class JwtServiceImpl implements IJwtService, IValidator<String> {
    private final IJwtUtil jwtUtil;

    @Autowired
    public JwtServiceImpl(IJwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public String generateAccessToken(String username, Long userId, Role role) {
        return jwtUtil.generateAccessToken(username, userId, role);
    }

    @Override
    public String getUsernameFromToken(String token) {
        return jwtUtil.getUserNameFromToken(token);
    }

    @Override
    public Long getUserIdFromToken(String token){return jwtUtil.getUserIdFromToken(token);}

    @Override
    public boolean validate(String token) {
        return StringUtils.hasText(token) && jwtUtil.validateAccessToken(token);
    }

    @Override
    public Role getUserRoleFromToken(String token) {
        return jwtUtil.getUserRoleFromToken(token);
    }
}
