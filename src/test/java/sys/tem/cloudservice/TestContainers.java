package sys.tem.cloudservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import sys.tem.cloudservice.controller.AuthController;
import sys.tem.cloudservice.exception.InvalidUserCredentialsException;
import sys.tem.cloudservice.security.jwt.Generator;
import sys.tem.cloudservice.security.model.dto.AuthToken;
import sys.tem.cloudservice.security.model.dto.Login;
import sys.tem.cloudservice.security.repository.UserRepository;

import java.lang.invoke.MethodHandles;

import static org.junit.jupiter.api.Assertions.*;
import static sys.tem.cloudservice.CloudserviceApplication.MI;

@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@SpringBootTest
@Testcontainers
@ExtendWith(SpringExtension.class)
public class TestContainers {
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("postgres")
            .withUsername("postgres")
            .withPassword("postgres");
    @Value("${valdef.user}")
    private String user;
    @Value("${valdef.password}")
    private String pass;

    private final AuthController authController;
    private final Generator generator;

    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    static void raiseDB() {
        log.info(MI, "---Start {} ---", MethodHandles.lookup().lookupClass().getTypeName());
        postgresContainer.start();
        System.setProperty("spring.datasource.url", postgresContainer.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgresContainer.getUsername());
        System.setProperty("spring.datasource.password", postgresContainer.getPassword());
    }

    @AfterAll
    static void destroyDB() {
        postgresContainer.stop();
        log.info(MI, "---End {} ---", MethodHandles.lookup().lookupClass().getTypeName());
    }

    @Test
    void loginTest() {
        // Запрос для аутентификации для имеющегося пользователя в базе
        Login login = new Login(
                user,
                pass);

        // Запрос аутентификации
        ResponseEntity<?> response = authController.login(login);


        // Проверяем статус ответа
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(AuthToken.class, response.getBody());

        // Извлекаем токен из ответа
        String token = ((AuthToken) response.getBody()).authToken();

        // Проверяем валидность
        assertTrue(generator.validateToken(token));
        assertEquals(login.login(), generator.getUsernameFromToken(token));

    }

    @Test
    void loginNotTest() {
        // Пользователь с другим паролем
        Login request1 = new Login(
                user,
                pass + "1");
        // Пользователь с несуществующим username
        Login request2 = new Login(
                "user1",
                pass);

        // Запрос аутентификации пользователь с другим паролем
        assertThrows(InvalidUserCredentialsException.class, () -> {
            authController.login(request1);
        });
        // Запрос аутентификации пользователь с другим именем
        assertThrows(InvalidUserCredentialsException.class, () -> {
            authController.login(request2);
        });

    }
}
