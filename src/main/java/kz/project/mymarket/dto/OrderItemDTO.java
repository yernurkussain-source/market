package kz.project.mymarket.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class OrderItemDTO {

    private Long id;

    @NotNull(message = "Product ID is required")
    private Long productId;

    private String productName;

    @NotNull
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    private Double price;
}