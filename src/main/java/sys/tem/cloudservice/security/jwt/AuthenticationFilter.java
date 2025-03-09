package sys.tem.cloudservice.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import sys.tem.cloudservice.security.service.UserDetalsImp;

import java.io.IOException;

import static java.util.Objects.nonNull;
import static org.springframework.util.StringUtils.hasText;

@Component
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {
    private static final String HEADER_AUTHORIZATION = "Auth-Token";
    private static final String PREFIX_BEARER = "Bearer ";

    private final Generator tokenGenerator;
    private final UserDetalsImp userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final var token = getTokenFromRequest(request);
        if (nonNull(token) && tokenGenerator.validateToken(token)) {
            final var username = tokenGenerator.getUsernameFromToken(token);
            final var userDetails = userDetailsService.loadUserByUsername(username);
            final var authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        final var bearerToken = request.getHeader(HEADER_AUTHORIZATION);
        return hasText(bearerToken) && bearerToken.startsWith(PREFIX_BEARER) ?
                bearerToken.substring(7) :
                null;
    }
}
