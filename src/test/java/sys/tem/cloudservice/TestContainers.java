package sys.tem.cloudservice;

import lombok.RequiredArgsConstructor;
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
import sys.tem.cloudservice.security.jwt.Generator;
import sys.tem.cloudservice.security.model.dto.AuthToken;
import sys.tem.cloudservice.security.model.dto.Login;
import sys.tem.cloudservice.security.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;

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
        postgresContainer.start();
        System.setProperty("spring.datasource.url", postgresContainer.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgresContainer.getUsername());
        System.setProperty("spring.datasource.password", postgresContainer.getPassword());
    }

    @AfterAll
    static void destroyDB() {
        postgresContainer.stop();
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
                pass+"1");
        // Пользователь с несуществующим username
        Login request2 = new Login(
                "user",
                pass);

        // Запрос аутентификации пользователь с другим паролем
        ResponseEntity<?> response = authController.login(request1);
        // Проверяем статус ответа
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        // Запрос аутентификации пользователь с несуществующим username
        response = authController.login(request1);
        // Проверяем статус ответа
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
