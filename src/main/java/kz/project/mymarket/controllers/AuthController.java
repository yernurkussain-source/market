package kz.project.mymarket.controllers;

import kz.project.mymarket.dto.*;
import kz.project.mymarket.entities.User;
import kz.project.mymarket.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "This email is already registered"));
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setFullname(dto.getFullname());
        user.setRole("USER");

        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Registration completed successfully"));
    }
}