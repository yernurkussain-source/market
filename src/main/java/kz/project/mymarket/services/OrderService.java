package kz.project.mymarket.services;

import kz.project.mymarket.dto.OrderDTO;
import kz.project.mymarket.dto.OrderItemDTO;
import kz.project.mymarket.entities.Order;
import kz.project.mymarket.entities.OrderItem;
import kz.project.mymarket.entities.Product;
import kz.project.mymarket.entities.User;
import kz.project.mymarket.repositories.OrderRepository;
import kz.project.mymarket.repositories.ProductRepository;
import kz.project.mymarket.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public List<Order> findByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public List<Order> findByUserEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        return orderRepository.findByUserId(user.getId());
    }

    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));
    }

    public Order save(Order order) {
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus("PENDING");
        if (order.getItems() != null) {
            order.getItems().forEach(item -> item.setOrder(order));
        }
        return orderRepository.save(order);
    }

    @Transactional
    public Order createFromDto(OrderDTO dto, String userEmail) {
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new IllegalArgumentException("An order must contain at least one product");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found: " + userEmail));

        Order order = new Order();
        order.setUser(user);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus("PENDING");

        List<OrderItem> items = new ArrayList<>();
        for (OrderItemDTO itemDto : dto.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + itemDto.getProductId()));

            Integer quantity = itemDto.getQuantity();
            if (quantity == null || quantity < 1) {
                throw new IllegalArgumentException("Product quantity must be at least 1");
            }
            if (product.getQuantity() < quantity) {
                throw new IllegalArgumentException("Not enough stock for product: " + product.getName());
            }

            product.setQuantity(product.getQuantity() - quantity);
            productRepository.save(product);

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(quantity);
            item.setPrice(product.getPrice());
            items.add(item);
        }

        order.setItems(items);
        return orderRepository.save(order);
    }

    @Transactional
    public Order createSingleProductOrder(String userEmail, Long productId, Integer quantity) {
        OrderDTO dto = new OrderDTO();
        OrderItemDTO item = new OrderItemDTO();
        item.setProductId(productId);
        item.setQuantity(quantity);
        dto.setItems(List.of(item));
        return createFromDto(dto, userEmail);
    }

    public Order updateStatus(Long id, String status) {
        Order order = findById(id);
        if (!List.of("PENDING", "CONFIRMED", "CANCELLED").contains(status)) {
            throw new IllegalArgumentException("Status must be PENDING, CONFIRMED, or CANCELLED");
        }
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public void delete(Long id) {
        findById(id);
        orderRepository.deleteById(id);
    }

    public OrderDTO toDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setStatus(order.getStatus());
        dto.setCreatedAt(order.getCreatedAt());
        if (order.getUser() != null) {
            dto.setUserId(order.getUser().getId());
            dto.setUserFullname(order.getUser().getFullname());
        }
        if (order.getItems() != null) {
            dto.setItems(order.getItems().stream().map(item -> {
                OrderItemDTO itemDto = new OrderItemDTO();
                itemDto.setId(item.getId());
                itemDto.setQuantity(item.getQuantity());
                itemDto.setPrice(item.getPrice());
                if (item.getProduct() != null) {
                    itemDto.setProductId(item.getProduct().getId());
                    itemDto.setProductName(item.getProduct().getName());
                }
                return itemDto;
            }).collect(Collectors.toList()));
        }
        return dto;
    }
}
