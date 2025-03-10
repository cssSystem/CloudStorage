package sys.tem.cloudservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import sys.tem.cloudservice.security.jwt.Generator;
import sys.tem.cloudservice.security.model.dto.AuthToken;
import sys.tem.cloudservice.security.model.dto.Login;
import sys.tem.cloudservice.security.service.AuthService;

import static sys.tem.cloudservice.CloudserviceApplication.MI;

@Log4j2
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final Generator generator;

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthToken> login(@RequestBody Login loginRequest) {
        log.info(MI, "Запрос аутентификации {} : ***", loginRequest.login());
        return ResponseEntity.ok().body(
                authService.login(loginRequest)
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "auth-token", required = false) String authToken) {
        authService.logout();

        if (authToken != null) {
            log.info(MI, "Отмена аутентификации пользователя ,,{},,", generator.getUsernameFromToken(generator.getTokenFromRequest(authToken)));
        } else {
            log.info(MI, "Пустой запрос отмены аутентификации");
        }
        return ResponseEntity.ok("Success logout");
    }

}
