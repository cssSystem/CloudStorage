package sys.tem.cloudservice.security.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import sys.tem.cloudservice.security.jwt.Generator;
import sys.tem.cloudservice.security.model.dto.AuthToken;
import sys.tem.cloudservice.security.model.dto.Login;
import sys.tem.cloudservice.security.model.entity.UserEnt;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private Generator generator;
    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Корректная аутентификация")
    void loginTest() {
        String username = "user@user.ru";
        String password = "password";
        String token = "token-ok";

        Login request = new Login(username, password);

        UserEnt mockUser = new UserEnt();
        mockUser.setUsername(username);
        mockUser.setPassword(password);

        // Аутентификация
        Authentication mockAuth = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(mockAuth);

        // Генерацию токена
        when(generator.generateToken(mockAuth)).thenReturn(token);

        // Запрос
        ResponseEntity<?> response = authService.login(request);

        // Проверка
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertInstanceOf(AuthToken.class, response.getBody());
        Assertions.assertEquals(token, ((AuthToken) response.getBody()).authToken());
    }

    @Test
    @DisplayName("Некорректная аутентификация")
    void shouldReturnErrorWhenAuthenticationFails() {
        String username = "user@user.ru";
        String password = "password";

        Login request = new Login(username, password);

        // аутентификация
        when(authenticationManager.authenticate(any())).thenThrow();

        // запрос
        ResponseEntity<?> response = authService.login(request);

        // Проверка
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}