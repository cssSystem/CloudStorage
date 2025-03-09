package sys.tem.cloudservice.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sys.tem.cloudservice.security.model.entity.UserEnt;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEnt, Long> {

    Optional<UserEnt> findByUsername(String login);

}
