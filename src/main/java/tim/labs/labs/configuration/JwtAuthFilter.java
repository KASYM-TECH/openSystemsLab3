package tim.labs.labs.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    public final UserAuthProvider userAuthProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (token != null) {
            try {
                var ctx = SecurityContextHolder.createEmptyContext();
                ctx.setAuthentication(userAuthProvider.validateToken(token));
                SecurityContextHolder.setContext(ctx);
            } catch (RuntimeException e) {
                SecurityContextHolder.clearContext();
                //throw e;
            }
        }
        filterChain.doFilter(request, response);
    }
}
