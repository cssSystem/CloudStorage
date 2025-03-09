package sys.tem.cloudservice.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import sys.tem.cloudservice.security.model.entity.UserEnt;
import sys.tem.cloudservice.security.repository.UserRepository;

@Configuration
@RequiredArgsConstructor
public class initUser {
    @Value("${valdef.user}")
    private String user;
    @Value("${valdef.password}")
    private String pass;
    private final UserRepository userRepository;

    @Bean
    public CommandLineRunner initUserDef() {
        return args -> {
            if (userRepository.findByUsername(user).isEmpty()) {
                userRepository.save(new UserEnt()
                        .setPassword(new BCryptPasswordEncoder().encode(pass))
                        .setUsername(user)
                );
            }
        };
    }
}
