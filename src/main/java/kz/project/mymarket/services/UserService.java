package kz.project.mymarket.services;

import kz.project.mymarket.entities.User;
import kz.project.mymarket.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
    }

    public User save(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("This email is already registered");
        }
        user.setRole(normalizeRole(user.getRole()));
        if (user.getPassword() != null && !user.getPassword().startsWith("$2")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    public User update(Long id, User user) {
        User existing = findById(id);
        if (!existing.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("This email is already registered");
        }
        existing.setFullname(user.getFullname());
        existing.setEmail(user.getEmail());
        existing.setRole(normalizeRole(user.getRole()));
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(existing);
    }

    public void delete(Long id) {
        findById(id);
        userRepository.deleteById(id);
    }

    private String normalizeRole(String role) {
        if (role == null || role.isBlank()) {
            return "USER";
        }
        String normalized = role.trim().toUpperCase();
        if (!normalized.equals("USER") && !normalized.equals("ADMIN")) {
            throw new IllegalArgumentException("Role must be USER or ADMIN");
        }
        return normalized;
    }
}
