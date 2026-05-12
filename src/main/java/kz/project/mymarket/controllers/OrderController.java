package kz.project.mymarket.controllers;

import jakarta.validation.Valid;
import kz.project.mymarket.dto.OrderDTO;
import kz.project.mymarket.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<OrderDTO> findAll() {
        return orderService.findAll().stream().map(orderService::toDTO).toList();
    }

    @GetMapping("/my")
    public List<OrderDTO> myOrders(Authentication authentication) {
        return orderService.findByUserEmail(authentication.getName()).stream().map(orderService::toDTO).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public OrderDTO findById(@PathVariable Long id) {
        return orderService.toDTO(orderService.findById(id));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<OrderDTO> findByUser(@PathVariable Long userId) {
        return orderService.findByUserId(userId).stream().map(orderService::toDTO).toList();
    }

    @PostMapping
    public ResponseEntity<OrderDTO> create(@Valid @RequestBody OrderDTO orderDTO, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.toDTO(orderService.createFromDto(orderDTO, authentication.getName())));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public OrderDTO updateStatus(@PathVariable Long id, @RequestParam String status) {
        return orderService.toDTO(orderService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        orderService.delete(id);
    }
}
