package sys.tem.cloudservice.security.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import sys.tem.cloudservice.exception.InvalidUserCredentialsException;
import sys.tem.cloudservice.security.jwt.Generator;
import sys.tem.cloudservice.security.model.dto.AuthToken;
import sys.tem.cloudservice.security.model.dto.Login;
import sys.tem.cloudservice.security.model.entity.UserEnt;

import java.lang.invoke.MethodHandles;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static sys.tem.cloudservice.CloudserviceApplication.MI;

@Log4j2
@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private Generator generator;
    @InjectMocks
    private AuthService authService;

    @BeforeAll
    public static void initTest() {
        log.info(MI, "---Start {} ---", MethodHandles.lookup().lookupClass().getTypeName());
    }

    @AfterAll
    public static void endTest() {
        log.info(MI, "---End {} ---", MethodHandles.lookup().lookupClass().getTypeName());
    }

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
        AuthToken authToken = authService.login(request);

        // Проверка
        Assertions.assertNotNull(authToken);
        Assertions.assertEquals(token, authToken.authToken());
    }

    @Test
    @DisplayName("Некорректная аутентификация")
    void shouldReturnErrorWhenAuthenticationFails() {
        String username = "user@user.ru";
        String password = "password";

        Login request = new Login(username, password);

        // аутентификация
        when(authenticationManager.authenticate(any())).thenThrow();

        // Проверка
        Assertions.assertThrows(InvalidUserCredentialsException.class, () -> authService.login(request));
    }
}