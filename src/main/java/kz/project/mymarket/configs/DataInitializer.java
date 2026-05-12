package kz.project.mymarket.configs;

import kz.project.mymarket.entities.User;
import kz.project.mymarket.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail("admin@mymarket.kz")) {
            User admin = new User();
            admin.setFullname("Admin");
            admin.setEmail("admin@mymarket.kz");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            userRepository.save(admin);
        }
    }
}
