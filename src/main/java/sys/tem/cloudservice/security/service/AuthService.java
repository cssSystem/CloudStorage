package sys.tem.cloudservice.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import sys.tem.cloudservice.model.dto.ErrorDTO;
import sys.tem.cloudservice.security.jwt.Generator;
import sys.tem.cloudservice.security.model.dto.AuthToken;
import sys.tem.cloudservice.security.model.dto.Login;


@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final Generator tokenGenerator;
    private String msgOkLogout = "Success logout";
    private ErrorDTO msgErrorLogin = new ErrorDTO("Bad credentials", 400);

    public ResponseEntity<?> login(Login loginRequest) {
        final var autToken = new UsernamePasswordAuthenticationToken(loginRequest.login(), loginRequest.password());
        try {
            final var authentication = authenticationManager.authenticate(autToken);
            final var token = tokenGenerator.generateToken(authentication);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return ResponseEntity.ok().body(new AuthToken(token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(msgErrorLogin);
        }
    }

    public ResponseEntity<String> logout(String authToken) {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(msgOkLogout);
    }
}
