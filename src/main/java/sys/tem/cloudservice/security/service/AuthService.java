package sys.tem.cloudservice.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import sys.tem.cloudservice.exception.InvalidUserCredentialsException;
import sys.tem.cloudservice.security.jwt.Generator;
import sys.tem.cloudservice.security.model.dto.AuthToken;
import sys.tem.cloudservice.security.model.dto.Login;

import static sys.tem.cloudservice.CloudserviceApplication.MI;

@Log4j2
@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final Generator tokenGenerator;

    public AuthToken login(Login loginRequest) {
        final var autToken = new UsernamePasswordAuthenticationToken(loginRequest.login(), loginRequest.password());
        try {
            final var authentication = authenticationManager.authenticate(autToken);
            final var token = tokenGenerator.generateToken(authentication);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info(MI, "Пользователь ,,{},, авторизован", loginRequest.login());
            return new AuthToken(token);
        } catch (Exception e) {
            log.info(MI, "Не корректные данные авторизации пользователя ,,{},,", loginRequest.login());
            throw new InvalidUserCredentialsException("Bad credentials");
        }
    }

    public void logout() {
        SecurityContextHolder.clearContext();
    }
}
