package kz.project.mymarket.controllers;

import jakarta.validation.Valid;
import kz.project.mymarket.entities.User;
import kz.project.mymarket.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<User> findAll() { return userService.findAll(); }

    @GetMapping("/{id}")
    public User findById(@PathVariable Long id) { return userService.findById(id); }

    @PostMapping
    public User create(@Valid @RequestBody User user) { return userService.save(user); }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { userService.delete(id); }
}
