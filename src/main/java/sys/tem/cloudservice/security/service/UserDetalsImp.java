package sys.tem.cloudservice.security.service;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import sys.tem.cloudservice.exception.UserNotFoundException;
import sys.tem.cloudservice.security.model.entity.UserEnt;
import sys.tem.cloudservice.security.repository.UserRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class UserDetalsImp implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UserNotFoundException {
        UserEnt userEnt = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("not user"));

        return new User(
                userEnt.getUsername(),
                userEnt.getPassword(),
                List.of()
        );
    }
}
