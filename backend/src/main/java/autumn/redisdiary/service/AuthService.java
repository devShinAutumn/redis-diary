
package autumn.redisdiary.service;

import autumn.redisdiary.entity.User;
import autumn.redisdiary.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User signup(String username, String email, String password) {
        var user = User.builder()
                .username(username)
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .roles("ROLE_USER")
                .createdAt(Instant.now())
                .build();
        return userRepository.save(user);
    }

    public User authenticate(String username, String password) {
        var opt = userRepository.findByUsername(username);
        if (opt.isEmpty()) return null;
        var user = opt.get();
        if (passwordEncoder.matches(password, user.getPasswordHash())) return user;
        return null;
    }
}
